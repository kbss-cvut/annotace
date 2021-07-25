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

import cz.cvut.kbss.textanalysis.configuration.MorphoditaConf;
import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

@Service
public class MorphoDitaServiceOnline implements MorphoDitaServiceAPI {

    private RestTemplateBuilder restTemplateBuilder;

    private MorphoditaConf conf;

    @Autowired
    public MorphoDitaServiceOnline(final RestTemplateBuilder restTemplateBuilder,
                                   final MorphoditaConf conf) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.conf = conf;
    }

    public List<List<MorphoDitaResultJson>> getMorphoDiteResultProcessed(String s) {
        final MorphoDitaResult morphoDitaResult = restTemplateBuilder.build().getForObject(
            conf.getServer() +
                "/tag?data=" + s + "&output=json",
            MorphoDitaResult.class);
        return morphoDitaResult.getResult();
    }
}
