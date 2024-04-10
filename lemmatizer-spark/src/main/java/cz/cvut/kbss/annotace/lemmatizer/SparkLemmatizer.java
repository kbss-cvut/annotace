package cz.cvut.kbss.annotace.lemmatizer;

import com.google.common.base.Strings;
import com.johnsnowlabs.nlp.DocumentAssembler;
import com.johnsnowlabs.nlp.IAnnotation;
import com.johnsnowlabs.nlp.JavaAnnotation;
import com.johnsnowlabs.nlp.LightPipeline;
import com.johnsnowlabs.nlp.SparkNLP;
import com.johnsnowlabs.nlp.annotators.LemmatizerModel;
import com.johnsnowlabs.nlp.annotators.Tokenizer;
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector;
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline;
import cz.cvut.kbss.annotace.configuration.SparkConf;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import cz.cvut.kbss.textanalysis.lemmatizer.model.SingleLemmaResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class SparkLemmatizer implements LemmatizerApi {

    private final SparkSession spark;

    private final Map<String, LightPipeline> pipelines = new HashMap<>();

    public SparkLemmatizer(SparkConf conf) {
        spark = SparkNLP.start(false, false, false, "2G", "", "", "", scala.collection.immutable.Map$.MODULE$.empty());
        conf.getLemmatizers().forEach((language, sparkObject) -> {
            try {
                log.info("Creating pipeline for lang {}", language);
                final LightPipeline pipeline;
                if (sparkObject.startsWith("pipeline:")) {
                    final String pipelineName = sparkObject.substring("pipeline:".length());
                    pipeline = new PretrainedPipeline(pipelineName, language).lightModel();
                    pipelines.put(language, pipeline);
                } else if (sparkObject.startsWith("model:")) {
                    final String modelName = sparkObject.substring("model:".length());
                    final DocumentAssembler documentAssembler = new DocumentAssembler();
                    documentAssembler.setInputCol("text");
                    documentAssembler.setOutputCol("document");
                    documentAssembler.setCleanupMode("disabled");

                    final SentenceDetector sentenceDetector = new SentenceDetector();
                    sentenceDetector.setInputCols(new String[]{"document"});
                    sentenceDetector.setOutputCol("sentence");
                    sentenceDetector.setExplodeSentences(false);

                    final Tokenizer tokenizer = new Tokenizer();
                    tokenizer.setInputCols(new String[]{"sentence"});
                    tokenizer.setOutputCol("token");

                    final List<String> chars = new ArrayList<>(Arrays.asList(tokenizer.getContextChars()));
                    chars.add("`");
                    tokenizer.setContextChars(chars.toArray(new String[]{}));

                    log.info(" - loading lemmatizer {} using language {}", modelName, language);
                    final LemmatizerModel lemmatizer = LemmatizerModel.pretrained(modelName, language);
                    lemmatizer.setInputCols(new String[]{"token"});
                    lemmatizer.setOutputCol("lemmas");
                    log.info(" - lemmatizer loaded.");
                    final Pipeline p =
                            new Pipeline().setStages(
                                    new PipelineStage[]{documentAssembler, sentenceDetector, tokenizer,
                                                        lemmatizer});
                    final PipelineModel m =
                            p.fit(spark.createDataset(Collections.emptyList(), Encoders.STRING()).toDF("text"));
                    pipeline = new LightPipeline(m, true);
                } else {
                    log.warn("Unsupported type of Spark object: '{}'", sparkObject);
                    return;
                }
                pipelines.put(language, pipeline);
                log.info(" - pipeline created.");
            } catch (Exception e) {
                log.warn("Lemmatizer not loaded due to an error.", e);
            }
        });
    }

    @Override
    public LemmatizerResult process(final String text, final String lang) {
        String[] labels = text.split("\n\n\n\n");
        final List<List<SingleLemmaResult>> results = new ArrayList<>();

        for (final String label : labels) {
            final Map<String, List<IAnnotation>> map = pipelines.get(lang).fullAnnotateJava(label);
            final List<IAnnotation> tokens = map.get("token");
            final List<SingleLemmaResult> res = new ArrayList<>();
            results.add(res);

            for (int i = 0; i < tokens.size(); i++) {
                final JavaAnnotation a = (JavaAnnotation) tokens.get(i);
                final SingleLemmaResult r = new SingleLemmaResult();
                r.setToken(a.result());
                if (i == 0 && a.getBegin() > 0) {
                    int spaceCount = 0;
                    int index = 0;
                    while (index < a.getBegin() && Character.isSpaceChar(label.charAt(index))) {
                        spaceCount++;
                        index++;
                    }
                    r.setLeadingSpaces(" ".repeat(spaceCount));
                }

                final JavaAnnotation aLemma = (JavaAnnotation) map.get("lemmas").get(i);
                r.setLemma(aLemma.result().replace("\u2018", "").replace("\u2019", ""));

                // TODO implement negated properly.
                r.setNegated(false);

                int startNext =
                        (tokens.size() - 1 == i) ? a.end() : ((JavaAnnotation) tokens.get(i + 1)).begin() - 1;
                r.setTrailingSpaces(Strings.repeat(" ", startNext - (a.end())));
                res.add(r);
            }
        }

        final LemmatizerResult result = new LemmatizerResult();
        result.setResult(results);
        result.setLemmatizer(this.getClass().getName());
        return result;
    }
}