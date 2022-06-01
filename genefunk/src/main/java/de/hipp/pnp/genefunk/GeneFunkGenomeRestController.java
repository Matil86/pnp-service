package de.hipp.pnp.genefunk;

import de.hipp.pnp.api.constants.UrlConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(UrlConstants.GENEFUNKGENOMERURL)
public class GeneFunkGenomeRestController {

    final GeneFunkGenomeService genomeService;

    public GeneFunkGenomeRestController(GeneFunkGenomeService genomeService) {
        this.genomeService = genomeService;
    }

    @GetMapping
    public List<GeneFunkGenome> getAllClasses() {
        return genomeService.getAllGenomes();
    }

}
