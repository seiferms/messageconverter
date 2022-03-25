package digital.frontiers.messageconverters.web;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ImageController {

    @GetMapping(value = "/{parameter}")
    public String retrieveImage(@PathVariable String parameter) {
        return parameter;
    }

}
