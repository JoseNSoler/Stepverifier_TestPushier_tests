package com.example.Spring.Webflux.Test;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@SpringBootTest
class SpringWebfluxTestApplicationTests {

	@Service
	public class Servicio {
		public Mono<String> buscarUno() {
			return Mono.just("Pedro");
		}
		public Flux<String> buscarTodos() {
			return Flux.just("Pedro", "Maria", "Jesus", "Carmen");
		}
		public Flux<String> buscarTodosLento() {
			return Flux.just("Pedro", "Maria", "Jesus", "Carmen").delaySequence(Duration.ofSeconds(20));
		}

		public Flux<String> buscarTodosFiltro() {
			Flux<String> source = Flux.just("John", "Monica", "Mark", "Cloe", "Frank", "Casper", "Olivia", "Emily", "Cate")
					.filter(name -> name.length() == 4)
					.map(String::toUpperCase);
			return source;
		}
	}

	@SpringBootTest
	class ServicioTest {
		@Autowired
		Servicio servicio;
		@Test
		void testMono() {
			Mono<String> uno = servicio.buscarUno();
			StepVerifier.create(uno).expectNext("Pedro").verifyComplete();
		}
		@Test
		void testVarios() {
			Flux<String> uno = servicio.buscarTodos();
			StepVerifier.create(uno).expectNext("Pedro").expectNext("Maria").expectNext("Jesus").expectNext("Carmen").verifyComplete();
		}

		@Test
		void testVariosLento() {
			Flux<String> uno = servicio.buscarTodosLento();
			StepVerifier.create(uno)
					.expectNext("Pedro")
					.thenAwait(Duration.ofSeconds(1))
					.expectNext("Maria")
					.thenAwait(Duration.ofSeconds(1))
					.expectNext("Jesus")
					.thenAwait(Duration.ofSeconds(1))
					.expectNext("Carmen")
					.thenAwait(Duration.ofSeconds(1)).verifyComplete();
		}
		@Test
		void testTodosFiltro() {
			Flux<String> source = servicio.buscarTodosFiltro();
			StepVerifier
					.create(source)
					.expectNext("JOHN")
					.expectNextMatches(name -> name.startsWith("MA"))
					.expectNext("CLOE", "CATE")
					.expectComplete()
					.verify();
		}
	}


	@Test
	void contextLoads() {
	}

}
