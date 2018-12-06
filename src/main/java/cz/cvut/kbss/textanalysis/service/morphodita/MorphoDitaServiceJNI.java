package cz.cvut.kbss.textanalysis.service.morphodita;

import cz.cuni.mff.ufal.morphodita.Forms;
import cz.cuni.mff.ufal.morphodita.TaggedLemma;
import cz.cuni.mff.ufal.morphodita.TaggedLemmas;
import cz.cuni.mff.ufal.morphodita.Tagger;
import cz.cuni.mff.ufal.morphodita.TokenRange;
import cz.cuni.mff.ufal.morphodita.TokenRanges;
import cz.cuni.mff.ufal.morphodita.Tokenizer;
import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service
public class MorphoDitaServiceJNI implements MorphoDitaServiceAPI {

    private static Logger logger = LoggerFactory.getLogger(MorphoDitaServiceJNI.class);

    private static Tagger tagger = null;

    private static final String taggerFileResource="czech-morfflex-pdt-161115-no_dia.tagger";

    static {
        try {
            logger.info("Finding {} ...", taggerFileResource);
            final String taggerFile =
                MorphoDitaServiceJNI.class.getResource("/czech-morfflex-pdt-161115-no_dia.tagger")
                                          .getFile();
            logger.info("Found at {}", taggerFile);
            logger.info("Loading tagger ... (looks up MorphoDita native library at {})",System.getProperty("java.library.path"));
            tagger = Tagger.load(new File(taggerFile).getAbsolutePath());
            logger.info("Tagger succesfully created");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tagger == null) {
            logger.warn(
                "Creating tagger failed.");
        }
    }

    public static boolean available() {
        return tagger != null;
    }

    @Override public List<List<MorphoDitaResultJson>> getMorphoDiteResultProcessed(String s) {
        final List<TokenRanges> tTr = new ArrayList<>();
        final List<Forms> tF = new ArrayList<>();
        final List<TaggedLemmas> tTl = new ArrayList<>();

        final Tokenizer tk = Tokenizer.newCzechTokenizer();
        tk.setText(s);

        while (true) {
            final Forms f = new Forms();
            final TokenRanges tr = new TokenRanges();
            final TaggedLemmas tl = new TaggedLemmas();
            tTr.add(tr);
            tTl.add(tl);
            tF.add(f);
            boolean x = tk.nextSentence(f, tr);
            tagger.tag(f, tl);
            if (!x) {
                break;
            }
        }

        return transform(tF, tTl, tTr);
    }

    private List<List<MorphoDitaResultJson>> transform(final List<Forms> lForms,
                                                       final List<TaggedLemmas> lTaggedLemmas,
                                                       final List<TokenRanges> lTokenRanges) {
        final List<List<MorphoDitaResultJson>> result = new ArrayList<>();

        for (int i = 0; i < lTaggedLemmas.size(); i++) {
            final TaggedLemmas tokenLemmas = lTaggedLemmas.get(i);
            final TokenRanges tokenRanges = lTokenRanges.get(i);
            final Forms forms = lForms.get(i);
            final List<MorphoDitaResultJson> sentence = new ArrayList<>();
            result.add(sentence);
            for (int j = 0; j < tokenLemmas.size(); j++) {
                final TaggedLemma taggedLemma = tokenLemmas.get(j);
                final TokenRange tokenRange = tokenRanges.get(j);
                final MorphoDitaResultJson token = new MorphoDitaResultJson();
                token.setLemma(taggedLemma.getLemma());
                token.setTag(taggedLemma.getTag());
                token.setToken(forms.get(j));

                final long end = tokenRange.getStart()+tokenRange.getLength();
                final long startNext = (j == tokenLemmas.size()-1) ? end :tokenRanges.get(j+1).getStart();

                String spaces = StringUtils.repeat(" ",(int) (startNext-end));
                token.setSpace( spaces );
                sentence.add(token);
                //logger.debug("TAG: " + taggedLemma.getTag() + ", LEMMA: " + taggedLemma.getLemma() + ", FORM: " + forms.get(j) + ", SPACES:\""+spaces+"\"");
            }
        }
        return result;
    }
}
