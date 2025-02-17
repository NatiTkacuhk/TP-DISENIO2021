package main.java.gestores;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.SortOrder;

import main.java.clases.Pasajero;
import main.java.daos.PasajeroDAO;
import main.java.dtos.PasajeroDTO;
import main.java.enums.ColumnaBuscarPasajeros;
import main.java.clases.Direccion;
import main.java.clases.Pais;
import main.java.excepciones.DocumentoRepetidoException;
import main.java.excepciones.InputInvalidaException;
import main.java.excepciones.ResponsableMenorException;
import main.java.excepciones.SinResultadosException;
import main.java.postgreImpl.PasajeroPostgreSQLImpl;

public class GestorPasajero {
	private static GestorPasajero instance;
	
	private PasajeroDAO pasajeroDAO;
	private GestorDireccion gestorDireccion;
	private GestorPaisProvincia gestorPaisProvincia;
	
	private GestorPasajero() {
		pasajeroDAO = new PasajeroPostgreSQLImpl();
	}
	
	public static GestorPasajero getInstance() {
		if (instance == null) instance = new GestorPasajero();
		
		return instance;
	}
	
	// Busca en la BD los pasajeros que cumplen con los filtro y devuelve la cantidad de resultados
	public Integer buscarCantidadPasajeros(PasajeroDTO filtros) throws SinResultadosException{	
		Integer cantPasajeros = pasajeroDAO.cantidadPasajeros(filtros);
		
		if (cantPasajeros == 0) throw new SinResultadosException();
		
		return cantPasajeros;
	}
	
	public List<PasajeroDTO> buscarPaginado(PasajeroDTO filtros, Integer tamPagina, Integer nroPagina, ColumnaBuscarPasajeros columna, SortOrder orden) {
		List<Pasajero> pasajeros = pasajeroDAO.buscarPasajerosPaginado(filtros, tamPagina, nroPagina, columna, orden);
		
		List<PasajeroDTO> pasajerosDTO = new ArrayList<>();
		
		for (Pasajero p : pasajeros) {
			pasajerosDTO.add(crearPasajeroDTOAcotado(p));
		}
		
		return pasajerosDTO;
	}
	
	public void validarDatosBusqueda(PasajeroDTO pasajeroDTO) throws InputInvalidaException{
		List<String> camposInvalidos = new ArrayList<String>();
		
		if (pasajeroDTO.getNombre() != null && !nombreApellidoValido(pasajeroDTO.getNombre())) camposInvalidos.add("Nombre");
		if (pasajeroDTO.getApellido() != null && !nombreApellidoValido(pasajeroDTO.getApellido())) camposInvalidos.add("Apellido");
		
		if (!camposInvalidos.isEmpty()) throw new InputInvalidaException(camposInvalidos);
		
	}
	
	private boolean nombreApellidoValido(String apellido) {
		return apellido.matches("[A-Z��������]+( [A-Z��������]+)*");
	}
	
	// Crea un PasajeroDTO con no todos los datos a partir de un pasajero
	private PasajeroDTO crearPasajeroDTOAcotado(Pasajero pasajero) {
		PasajeroDTO pasajeroDTO = new PasajeroDTO();
		
		pasajeroDTO.setId(pasajero.getId());
		pasajeroDTO.setNombre(pasajero.getNombre());
		pasajeroDTO.setApellido(pasajero.getApellido());
		pasajeroDTO.setTipoDocumento(pasajero.getTipoDocumento());
		pasajeroDTO.setNumeroDoc(pasajero.getDocumento());
		
		return pasajeroDTO;
	}
	
	public void validarDatosPasajero(PasajeroDTO pasajeroDTO) throws DocumentoRepetidoException {
		
		// En principio los datos ya tendr�an que ser v�lidos porque se validaron en la interfaz
		
		// Ver si ya existe en el sistema
		List<Pasajero> listaPasajeros = pasajeroDAO.buscarPorDocumento(pasajeroDTO.getTipoDocumento(), pasajeroDTO.getNumeroDoc());
		
		if (!listaPasajeros.isEmpty()) {
			throw new DocumentoRepetidoException();
		}
		
		
	}
	
	public void crearPasajero(PasajeroDTO pasajeroDTO) {
		
		gestorDireccion = GestorDireccion.getInstance();
		Direccion direccion = gestorDireccion.crearDireccion(pasajeroDTO.getDireccion());
		
		gestorPaisProvincia = GestorPaisProvincia.getInstance();
		Pais paisNacionalidad = gestorPaisProvincia.buscarPaisPorId(pasajeroDTO.getIdNacionalidad());
		
		Pasajero pasajero = new Pasajero(pasajeroDTO, direccion, paisNacionalidad);
		
		pasajeroDAO.guardar(pasajero);
	}
	
	public List<Pasajero> buscarPasajeros(List<PasajeroDTO> listaPasajerosDTO) {
		List<Integer> idsPasajeros = listaPasajerosDTO
										.stream()
										.map(p -> p.getId())
										.collect(Collectors.toList());
		
		return pasajeroDAO.buscarPasajeros(idsPasajeros);
	}
	
	public void validarEdad(Integer idPasajero) throws ResponsableMenorException {
		Pasajero p = pasajeroDAO.buscar(idPasajero);
		
		if (p.getEdad() < 18) throw new ResponsableMenorException();
	}
	
	
}
