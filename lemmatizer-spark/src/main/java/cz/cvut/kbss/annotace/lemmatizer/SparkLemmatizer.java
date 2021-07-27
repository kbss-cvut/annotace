package cz.cvut.kbss.annotace.lemmatizer;

import com.google.common.base.Strings;
import com.johnsnowlabs.nlp.DocumentAssembler;
import com.johnsnowlabs.nlp.JavaAnnotation;
import com.johnsnowlabs.nlp.LightPipeline;
import com.johnsnowlabs.nlp.SparkNLP;
import com.johnsnowlabs.nlp.annotators.LemmatizerModel;
import com.johnsnowlabs.nlp.annotators.Tokenizer;
import com.johnsnowlabs.nlp.annotators.sbd.pragmatic.SentenceDetector;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import cz.cvut.kbss.textanalysis.lemmatizer.model.SingleLemmaResult;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

@Service
public class SparkLemmatizer implements LemmatizerApi {

    private SparkSession spark;

    @PostConstruct
    public void init() {
        spark = SparkNLP.start(false, false, false, "2G");
//        spark = SparkNLP.start(falseMorphoditaConf, false);
    }

    @Override
    public LemmatizerResult process(String s) {
        final DocumentAssembler documentAssembler = new DocumentAssembler();
        documentAssembler.setInputCol("text");
        documentAssembler.setOutputCol("document");
        documentAssembler.setCleanupMode("disabled");

        final SentenceDetector sentenceDetector = new SentenceDetector();
        sentenceDetector.setInputCols(new String[] {"document"});
        sentenceDetector.setOutputCol("sentence");
        sentenceDetector.setExplodeSentences(false);

        final Tokenizer tokenizer = new Tokenizer();
        tokenizer.setInputCols(new String[] {"sentence"});
        tokenizer.setOutputCol("token");

//        final PerceptronModel perceptronModel = PerceptronModel.pretrained("pos_cac", "cs");
//        perceptronModel.setInputCols(new String[] {"sentence", "token"});
//        perceptronModel.setOutputCol("pos");

        final LemmatizerModel lemmatizer = LemmatizerModel.pretrained("lemma", "cs");
        lemmatizer.setInputCols(new String[] {"token"});
        lemmatizer.setOutputCol("lemma");

        final Pipeline pipeline = new Pipeline().setStages(
            new PipelineStage[] {documentAssembler, sentenceDetector, tokenizer,
//                perceptronModel,
                lemmatizer});
        final Dataset data = spark.createDataset(Arrays.asList(s), Encoders.STRING()).toDF("text");
        final PipelineModel model = pipeline.fit(data);

        final LightPipeline lPipeline = new LightPipeline(model, true);
//        final Map<String,List<String>> map = lPipeline.annotateJava(s);
        final Map<String, List<JavaAnnotation>> map = lPipeline.fullAnnotateJava(s);

        final List<List<SingleLemmaResult>> res = new ArrayList<>();

        map.get("sentence").forEach(sentence -> {
            res.add(new ArrayList<>());
        });
        final List<JavaAnnotation> tokens = map.get("token");

        for (int i = 0; i < tokens.size(); i++) {
            final JavaAnnotation a = tokens.get(i);
            final SingleLemmaResult r = new SingleLemmaResult();
            r.setToken(a.result());

            final JavaAnnotation aLemma = map.get("lemma").get(i);
            r.setLemma(aLemma.result());

            // TODO
            r.setNegated(false);

            int startNext =
                (tokens.size() - 1 == i) ? a.end() : tokens.get(i + 1).begin();
            r.setSpaces(Strings.repeat(" ", startNext - (a.end())));

            int sentence = Integer.parseInt(a.metadata().get("sentence"));
            res.get(sentence).add(r);
        }

        final LemmatizerResult result = new LemmatizerResult();
        result.setResult(res);
        result.setLemmatizer(this.getClass().getName());
        return result;
    }
}