package org.example.controllers;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.example.dto.NewReaderDTO;
import org.example.entity.Reader;
import org.example.services.ReaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("api/v1/readers")
@Validated
public class ReaderController {

  private final ReaderService readerService;

  public ReaderController(ReaderService readerService) {
    this.readerService = readerService;
  }

  @GetMapping
  public ResponseEntity<List<Reader>> getAllReaders() {
    return ResponseEntity.ok(readerService.findAllReaders());
  }

  @PostMapping
  public ResponseEntity<?> saveReader(@RequestBody @Valid NewReaderDTO newReaderDTO) {
    var reader = new Reader(newReaderDTO);
    var savedReader = readerService.addNewReader(reader);
    var uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{id}")
        .build(String.valueOf(savedReader.getId()));
    return ResponseEntity.created(uri).body(savedReader);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getReaderById(
      @PathVariable("id") @Positive(message = "Min value have to be 1") Long id) {
    return readerService.findById(id).map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

}
