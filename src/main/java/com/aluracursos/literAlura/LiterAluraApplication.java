package com.aluracursos.literAlura;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.aluracursos.literAlura.service.AutorService;
import com.aluracursos.literAlura.service.LibroService;
import com.aluracursos.literAlura.service.MenuService;

@SpringBootApplication
public class LiterAluraApplication implements CommandLineRunner {


	@Autowired
	private LibroService libroService;
	@Autowired
	private AutorService autorService;

	public static void main(String[] args) {
		SpringApplication.run(LiterAluraApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
	 MenuService menuService = new MenuService(libroService,autorService);

		menuService.menu();

	}
}
