package tech.jhipster.beer.dummy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dummy")
public class DummyResource {

  @GetMapping
  public String getDummy() {
    return "hello world!";
  }
}
