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
package cz.cvut.kbss.textanalysis.rest;

import cz.cvut.kbss.textanalysis.model.MorphoDitaResultJson;
import cz.cvut.kbss.textanalysis.service.morphodita.MorphoDitaServiceAPI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController public class MorphoDitaController {

    @Autowired private MorphoDitaServiceAPI morphoDitaService;

    @RequestMapping("/morphodita-result-processed")
    public List<List<MorphoDitaResultJson>> getMorphoDitaResultProcessed(
        @RequestParam("textChunk") String textChunk) {
        return morphoDitaService.getMorphoDiteResultProcessed(textChunk);
    }
}