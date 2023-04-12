/**
 * Annotac Copyright (C) 2019 Czech Technical University in Prague
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <https://www.gnu.org/licenses/>. © 2019 GitHub, Inc.
 */

package cz.cvut.kbss.annotace.lemmatizer;

import cz.cuni.mff.ufal.morphodita.Forms;
import cz.cuni.mff.ufal.morphodita.TaggedLemma;
import cz.cuni.mff.ufal.morphodita.TaggedLemmas;
import cz.cuni.mff.ufal.morphodita.Tagger;
import cz.cuni.mff.ufal.morphodita.TokenRange;
import cz.cuni.mff.ufal.morphodita.TokenRanges;
import cz.cuni.mff.ufal.morphodita.Tokenizer;
import cz.cvut.kbss.annotace.configuration.MorphoditaConf;
import cz.cvut.kbss.textanalysis.lemmatizer.LemmatizerApi;
import cz.cvut.kbss.textanalysis.lemmatizer.model.LemmatizerResult;
import cz.cvut.kbss.textanalysis.lemmatizer.model.SingleLemmaResult;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Primary
@Service
@Scope("singleton")
@Slf4j
public class MorphoDitaServiceJNI implements LemmatizerApi {

    private Map<String, Tagger> taggers = new HashMap<>();

    @Autowired
    public MorphoDitaServiceJNI(MorphoditaConf conf) {
        conf.getTaggers().forEach((lang, taggerPath) -> {
            try {
                log.info("Finding {} ...", taggerPath);
                if (!new File(taggerPath).exists()) {
                    log.info("Tagger file {} for language {} not found.", taggerPath, lang);
                    return;
                }
                log.info("Found at {}", taggerPath);
                log.info("Loading tagger ... (looks up MorphoDita native library at {})",
                    System.getProperty("java.library.path"));
                Tagger tagger = Tagger.load(taggerPath);
                if (tagger == null) {
                    log.warn(
                        "Creating tagger failed.");
                } else {
                    taggers.put(lang,tagger);
                    log.info("Tagger {} for lang {} successfully created.", tagger, lang);
                }
            } catch (Exception e) {
                log.info("Creating tagger {} for lang {} failed.", taggerPath, lang);
            }
        });
    }

    @Override
    public LemmatizerResult process(String s, String lang) {
        final List<TokenRanges> tTr = new ArrayList<>();
        final List<Forms> tF = new ArrayList<>();
        final List<TaggedLemmas> tTl = new ArrayList<>();

        final Tokenizer tk =
            lang.equals("en") ? Tokenizer.newEnglishTokenizer() : Tokenizer.newCzechTokenizer();
        tk.setText(s);

        Tagger tagger = taggers.get(lang);
        if (tagger == null) {
            throw new RuntimeException("No tagger for language " + lang + " available.");
        }

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

        final List<List<SingleLemmaResult>> result = transform(s, tF, tTl, tTr);
        final LemmatizerResult result2 = new LemmatizerResult();
        result2.setResult(result);
        result2.setLemmatizer(this.getClass().getName());
        return result2;
    }

    private List<List<SingleLemmaResult>> transform(final String s,
                                                    final List<Forms> lForms,
                                                    final List<TaggedLemmas> lTaggedLemmas,
                                                    final List<TokenRanges> lTokenRanges) {
        final List<List<SingleLemmaResult>> result = new ArrayList<>();

        for (int i = 0; i < lTaggedLemmas.size(); i++) {
            final TaggedLemmas tokenLemmas = lTaggedLemmas.get(i);
            final TokenRanges tokenRanges = lTokenRanges.get(i);
            final Forms forms = lForms.get(i);
            final List<SingleLemmaResult> sentence = new ArrayList<>();
            result.add(sentence);
            for (int j = 0; j < tokenLemmas.size(); j++) {
                final TaggedLemma taggedLemma = tokenLemmas.get(j);
                final TokenRange tokenRange = tokenRanges.get(j);
                final SingleLemmaResult token = new SingleLemmaResult();
                token.setLemma(taggedLemma.getLemma());
                token.setNegated(taggedLemma.getTag().length() > 10 && taggedLemma.getTag().charAt(10) == 'N');
                token.setToken(forms.get(j));

                final long end = tokenRange.getStart() + tokenRange.getLength();
                final long startNext =
                    (j == tokenLemmas.size() - 1) ? end : tokenRanges.get(j + 1).getStart();

                String spaces = " ".repeat((int) (startNext - end));
                if (spaces.equals("") && (s.contains(forms.get(j).concat(" ")) ||
                    (s.contains(forms.get(j).concat("\r"))) ||
                    (s.contains(forms.get(j).concat("\n"))))) {
                    token.setSpaces(" ");
                } else {
                    token.setSpaces(spaces);
                }
                token.setSpaces(spaces);
                sentence.add(token);
            }
        }
        return result;
    }
}
