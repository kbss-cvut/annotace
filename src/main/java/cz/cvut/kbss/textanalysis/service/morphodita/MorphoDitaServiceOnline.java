/**
 * Annotac
 * Copyright (C) 2019 Czech Technical University in Prague
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * © 2019 GitHub, Inc.
 */
package cz.cvut.kbss.textanalysis.service.morphodita;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResult;
import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MorphoDitaServiceOnline implements MorphoDitaServiceAPI {
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    public List<List<MorphoDitaResultJson>> getMorphoDiteResultProcessed(String s) {
        final MorphoDitaResult morphoDitaResult = restTemplateBuilder.build().getForObject(
            "http://lindat.mff.cuni.cz/services/morphodita/api/tag?data=" + s +"&output=json",
            MorphoDitaResult.class);
        return morphoDitaResult.getResult();
    }
}
