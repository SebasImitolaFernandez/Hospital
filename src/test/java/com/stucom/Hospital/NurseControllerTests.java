package com.stucom.Hospital;
 
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 
import java.util.Collections;
import java.util.List;
import java.util.Optional;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
 
/**
 * Tests unitarios de NurseController.
 * Se usa MockMvc en modo standalone + Mockito: no se levanta el contexto de Spring
 * ni se necesita base de datos, así que corren rápido en el pipeline (mvn test).
 */
@ExtendWith(MockitoExtension.class)
class NurseControllerTests {
 
	@Mock
	private NurseRepository nurseRepository;
 
	@InjectMocks
	private NurseController nurseController;
 
	private MockMvc mockMvc;
 
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(nurseController).build();
	}
 
	// ---------- Helpers ----------
 
	private static Nurse buildNurse(int id, String name, String user, String password) {
		Nurse n = new Nurse();
		n.setId(id);
		n.setName(name);
		n.setUser(user);
		n.setPassword(password);
		return n;
	}
 
	private static String json(String name, String user, String password) {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		if (name != null) {
			sb.append("\"name\":\"").append(name).append("\"");
			first = false;
		}
		if (user != null) {
			if (!first) sb.append(",");
			sb.append("\"user\":\"").append(user).append("\"");
			first = false;
		}
		if (password != null) {
			if (!first) sb.append(",");
			sb.append("\"password\":\"").append(password).append("\"");
		}
		return sb.append("}").toString();
	}
 
	// ---------- GET /nurse/index ----------
 
	@Nested
	@DisplayName("GET /nurse/index")
	class GetAll {
 
		@Test
		@DisplayName("Devuelve 200 con la lista de enfermeras")
		void returnsAllNurses() throws Exception {
			when(nurseRepository.findAll()).thenReturn(List.of(
					buildNurse(1, "Ana", "ana", "1234"),
					buildNurse(2, "Luis", "luis", "abcd")));
 
			mockMvc.perform(get("/nurse/index"))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					.andExpect(jsonPath("$.length()").value(2))
					.andExpect(jsonPath("$[0].name").value("Ana"))
					.andExpect(jsonPath("$[1].user").value("luis"));
		}
 
		@Test
		@DisplayName("Devuelve 200 con lista vacía si no hay enfermeras")
		void returnsEmptyList() throws Exception {
			when(nurseRepository.findAll()).thenReturn(Collections.emptyList());
 
			mockMvc.perform(get("/nurse/index"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.length()").value(0));
		}
	}
 
	// ---------- POST /nurse/login ----------
 
	@Nested
	@DisplayName("POST /nurse/login")
	class Login {
 
		@Test
		@DisplayName("Credenciales correctas -> 200 y true")
		void loginOk() throws Exception {
			when(nurseRepository.findByUserAndPassword("ana", "1234"))
					.thenReturn(Optional.of(buildNurse(1, "Ana", "ana", "1234")));
 
			mockMvc.perform(post("/nurse/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json(null, "ana", "1234")))
					.andExpect(status().isOk())
					.andExpect(content().string("true"));
		}
 
		@Test
		@DisplayName("Credenciales incorrectas -> 401 y false")
		void loginFails() throws Exception {
			when(nurseRepository.findByUserAndPassword("ana", "mala"))
					.thenReturn(Optional.empty());
 
			mockMvc.perform(post("/nurse/login")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json(null, "ana", "mala")))
					.andExpect(status().isUnauthorized())
					.andExpect(content().string("false"));
		}
	}
 
	// ---------- GET /nurse/name/{name} ----------
 
	@Nested
	@DisplayName("GET /nurse/name/{name}")
	class FindByName {
 
		@Test
		@DisplayName("Hay coincidencias -> 200 con la lista")
		void found() throws Exception {
			when(nurseRepository.findByNameContainingIgnoreCase("an"))
					.thenReturn(List.of(buildNurse(1, "Ana", "ana", "1234")));
 
			mockMvc.perform(get("/nurse/name/an"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.length()").value(1))
					.andExpect(jsonPath("$[0].name").value("Ana"));
		}
 
		@Test
		@DisplayName("Sin coincidencias -> 404")
		void notFound() throws Exception {
			when(nurseRepository.findByNameContainingIgnoreCase("zzz"))
					.thenReturn(Collections.emptyList());
 
			mockMvc.perform(get("/nurse/name/zzz"))
					.andExpect(status().isNotFound());
		}
	}
 
	// ---------- GET /nurse/id/{id} ----------
 
	@Nested
	@DisplayName("GET /nurse/id/{id}")
	class GetById {
 
		@Test
		@DisplayName("Existe -> 200 con la enfermera")
		void found() throws Exception {
			when(nurseRepository.findById(1))
					.thenReturn(Optional.of(buildNurse(1, "Ana", "ana", "1234")));
 
			mockMvc.perform(get("/nurse/id/1"))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(1))
					.andExpect(jsonPath("$.name").value("Ana"));
		}
 
		@Test
		@DisplayName("No existe -> 404")
		void notFound() throws Exception {
			when(nurseRepository.findById(99)).thenReturn(Optional.empty());
 
			mockMvc.perform(get("/nurse/id/99"))
					.andExpect(status().isNotFound());
		}
 
		@Test
		@DisplayName("Id no numérico -> 400")
		void invalidId() throws Exception {
			mockMvc.perform(get("/nurse/id/abc"))
					.andExpect(status().isBadRequest());
		}
	}
 
	// ---------- POST /nurse ----------
 
	@Nested
	@DisplayName("POST /nurse (crear)")
	class Create {
 
		@Test
		@DisplayName("Datos válidos -> 201 con la enfermera creada")
		void created() throws Exception {
			when(nurseRepository.existsByUser("ana")).thenReturn(false);
			when(nurseRepository.save(any(Nurse.class))).thenAnswer(inv -> {
				Nurse n = inv.getArgument(0);
				n.setId(10);
				return n;
			});
 
			mockMvc.perform(post("/nurse")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana", "ana", "1234")))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.id").value(10))
					.andExpect(jsonPath("$.name").value("Ana"))
					.andExpect(jsonPath("$.user").value("ana"));
		}
 
		@Test
		@DisplayName("Ignora el id enviado por el cliente (lo pone a 0 antes de guardar)")
		void resetsIdBeforeSaving() throws Exception {
			when(nurseRepository.existsByUser("ana")).thenReturn(false);
			when(nurseRepository.save(any(Nurse.class))).thenAnswer(inv -> inv.getArgument(0));
 
			mockMvc.perform(post("/nurse")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"id\":55,\"name\":\"Ana\",\"user\":\"ana\",\"password\":\"1234\"}"))
					.andExpect(status().isCreated());
 
			ArgumentCaptor<Nurse> captor = ArgumentCaptor.forClass(Nurse.class);
			verify(nurseRepository).save(captor.capture());
			assertEquals(0, captor.getValue().getId());
		}
 
		@Test
		@DisplayName("Usuario ya existente -> 409")
		void conflict() throws Exception {
			when(nurseRepository.existsByUser("ana")).thenReturn(true);
 
			mockMvc.perform(post("/nurse")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana", "ana", "1234")))
					.andExpect(status().isConflict());
 
			verify(nurseRepository, never()).save(any());
		}
 
		@Test
		@DisplayName("Falta el nombre -> 400")
		void missingName() throws Exception {
			mockMvc.perform(post("/nurse")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json(null, "ana", "1234")))
					.andExpect(status().isBadRequest());
 
			verify(nurseRepository, never()).save(any());
		}
 
		@Test
		@DisplayName("Usuario en blanco -> 400")
		void blankUser() throws Exception {
			mockMvc.perform(post("/nurse")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana", "   ", "1234")))
					.andExpect(status().isBadRequest());
 
			verify(nurseRepository, never()).save(any());
		}
 
		@Test
		@DisplayName("Falta la contraseña -> 400")
		void missingPassword() throws Exception {
			mockMvc.perform(post("/nurse")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana", "ana", null)))
					.andExpect(status().isBadRequest());
 
			verify(nurseRepository, never()).save(any());
		}
	}
 
	// ---------- DELETE /nurse/{id} ----------
 
	@Nested
	@DisplayName("DELETE /nurse/{id}")
	class Delete {
 
		@Test
		@DisplayName("Existe -> 200 y se borra")
		void deleted() throws Exception {
			when(nurseRepository.findById(1))
					.thenReturn(Optional.of(buildNurse(1, "Ana", "ana", "1234")));
 
			mockMvc.perform(delete("/nurse/1"))
					.andExpect(status().isOk());
 
			verify(nurseRepository).deleteById(1);
		}
 
		@Test
		@DisplayName("No existe -> 404 y no se borra nada")
		void notFound() throws Exception {
			when(nurseRepository.findById(99)).thenReturn(Optional.empty());
 
			mockMvc.perform(delete("/nurse/99"))
					.andExpect(status().isNotFound());
 
			verify(nurseRepository, never()).deleteById(any());
		}
	}
 
	// ---------- PUT /nurse/{id} ----------
 
	@Nested
	@DisplayName("PUT /nurse/{id} (actualizar)")
	class Update {
 
		@Test
		@DisplayName("Datos válidos, mismo usuario -> 200 con los datos actualizados")
		void updatedSameUser() throws Exception {
			when(nurseRepository.findById(1))
					.thenReturn(Optional.of(buildNurse(1, "Ana", "ana", "1234")));
			when(nurseRepository.save(any(Nurse.class))).thenAnswer(inv -> inv.getArgument(0));
 
			mockMvc.perform(put("/nurse/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana María", "ana", "nueva")))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(1))
					.andExpect(jsonPath("$.name").value("Ana María"))
					.andExpect(jsonPath("$.password").doesNotExist());
 
			// Si el usuario no cambia, no hace falta comprobar si existe
			verify(nurseRepository, never()).existsByUser(anyString());
		}
 
		@Test
		@DisplayName("Cambio a un usuario libre -> 200")
		void updatedNewFreeUser() throws Exception {
			when(nurseRepository.findById(1))
					.thenReturn(Optional.of(buildNurse(1, "Ana", "ana", "1234")));
			when(nurseRepository.existsByUser("ana2")).thenReturn(false);
			when(nurseRepository.save(any(Nurse.class))).thenAnswer(inv -> inv.getArgument(0));
 
			mockMvc.perform(put("/nurse/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana", "ana2", "1234")))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.user").value("ana2"));
		}
 
		@Test
		@DisplayName("Cambio a un usuario ya ocupado -> 409")
		void conflict() throws Exception {
			when(nurseRepository.findById(1))
					.thenReturn(Optional.of(buildNurse(1, "Ana", "ana", "1234")));
			when(nurseRepository.existsByUser("luis")).thenReturn(true);
 
			mockMvc.perform(put("/nurse/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana", "luis", "1234")))
					.andExpect(status().isConflict());
 
			verify(nurseRepository, never()).save(any());
		}
 
		@Test
		@DisplayName("No existe -> 404")
		void notFound() throws Exception {
			when(nurseRepository.findById(99)).thenReturn(Optional.empty());
 
			mockMvc.perform(put("/nurse/99")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("Ana", "ana", "1234")))
					.andExpect(status().isNotFound());
 
			verify(nurseRepository, never()).save(any());
		}
 
		@Test
		@DisplayName("Datos inválidos -> 400")
		void badRequest() throws Exception {
			when(nurseRepository.findById(1))
					.thenReturn(Optional.of(buildNurse(1, "Ana", "ana", "1234")));
 
			mockMvc.perform(put("/nurse/1")
					.contentType(MediaType.APPLICATION_JSON)
					.content(json("", "ana", "1234")))
					.andExpect(status().isBadRequest());
 
			verify(nurseRepository, never()).save(any());
		}
	}
}