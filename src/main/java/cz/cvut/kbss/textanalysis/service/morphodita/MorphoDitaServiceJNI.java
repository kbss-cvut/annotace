/**
 * Annotac Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.  If
 * not, see <https://www.gnu.org/licenses/>. © 2019 GitHub, Inc.
 */

package cz.cvut.kbss.textanalysis.service.morphodita;

import cz.cuni.mff.ufal.morphodita.Forms;
import cz.cuni.mff.ufal.morphodita.TaggedLemma;
import cz.cuni.mff.ufal.morphodita.TaggedLemmas;
import cz.cuni.mff.ufal.morphodita.Tagger;
import cz.cuni.mff.ufal.morphodita.TokenRange;
import cz.cuni.mff.ufal.morphodita.TokenRanges;
import cz.cuni.mff.ufal.morphodita.Tokenizer;
import cz.cvut.kbss.textanalysis.configuration.MorphoditaConf;
import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Primary
@Service
@Scope("singleton")
@Slf4j
public class MorphoDitaServiceJNI implements MorphoDitaServiceAPI {

    private Tagger tagger;

    @Autowired
    public MorphoDitaServiceJNI(MorphoditaConf conf) {
        try {
            log.info("Finding {} ...", conf.getTagger());
            if (!new File(conf.getTagger()).exists()) {
                log.info("No Morphodita tagger file found.");
                return;
            }
            log.info("Found at {}", conf.getTagger());
            log.info("Loading tagger ... (looks up MorphoDita native library at {})",
                System.getProperty("java.library.path"));
            tagger = Tagger.load(conf.getTagger());
            log.info("Tagger succesfully created");
            if (tagger == null) {
                log.warn(
                    "Creating tagger failed.");
            }
        } catch (Exception e) {
            log.info("Creating tagger failed.", e);
        }
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

                final long end = tokenRange.getStart() + tokenRange.getLength();
                final long startNext =
                    (j == tokenLemmas.size() - 1) ? end : tokenRanges.get(j + 1).getStart();

                String spaces = StringUtils.repeat(" ", (int) (startNext - end));
                token.setSpace(spaces);
                sentence.add(token);
                //logger.debug("TAG: " + taggedLemma.getTag() + ", LEMMA: " + taggedLemma
                // .getLemma() + ", FORM: " + forms.get(j) + ", SPACES:\""+spaces+"\"");
            }
        }
        return result;
    }
}
