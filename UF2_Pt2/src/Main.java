import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Main {

	static final String url = "jdbc:mysql://localhost:3306/bd_videoclub";

	public static void main(String[] args) throws ParseException {
		connect();
		menu();
	}

	public static void connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, "root", "");
			System.out.println("BD Videoclub conectada.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public static void menu() throws ParseException {
		Scanner lector = new Scanner(System.in);
		int i = 0;
		while (i != 4) {
			System.out.println("\nMENU");
			System.out.println("   1. DONAR D'ALTA UN SOCI");
			System.out.println("   2. MODIFICAR CUOTA DE SOCI");
			System.out.println("   3. ELIMINAR UN SOCI");
			System.out.println("   4. SORTIR");
			System.out.print("Escull una opcio: ");
			i = lector.nextInt();
			if (i > 0 && i < 5) {
				switch (i) {
				case 1:
					altaSoci();
					break;
				case 2:
					modificarCuota();
					break;
				case 3:
					eliminarSoci();
					break;
				default:
					System.out.println("\nAdeu!");
					break;
				}
			} else
				System.out.println("\nError! Valor incorrecte.");
		}
	}

	public static void altaSoci() throws ParseException {
		Scanner lectorInt = new Scanner(System.in);
		Scanner lectorString = new Scanner(System.in);
		// datos
		System.out.println("\nInserta COD SOC:");
		int cod_soc = lectorInt.nextInt();
		System.out.println("Inserta NOMBRE:");
		String nombre = lectorString.nextLine();
		System.out.println("Inserta APELLIDOS:");
		String apellidos = lectorString.nextLine();
		System.out.println("Inserta DIRECCION:");
		String direccion = lectorString.nextLine();
		System.out.println("Inserta TELEFONO:");
		String telefono = lectorString.nextLine();
		System.out.println("Inserta POBLACION:");
		String poblacion = lectorString.nextLine();
		System.out.println("Inserta CP:");
		String cp = lectorString.nextLine();
		System.out.println("Inserta PROVINCIA:");
		String provincia = lectorString.nextLine();
		System.out.println("Inserta PAIS:");
		String pais = lectorString.nextLine();
		System.out.println("Inserta EDAD:");
		int edad = lectorInt.nextInt();
		System.out.println("Inserta FECHA ALTA (yyyy-MM-dd):");
		String fecha = lectorString.nextLine();
		Date parse_fecha = new SimpleDateFormat("yyyy-MM-dd").parse(fecha);
		java.sql.Date fecha_alta = new java.sql.Date(parse_fecha.getTime());
		System.out.println("Inserta CUOTA:");
		int cuota = lectorInt.nextInt();
		// insert
		String query = "INSERT INTO socio (cod_soc, nombre, apellidos, direccion, telefono, poblacion, cp, provincia, pais, edad, fechaalta, cuota) "
				+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			Connection conn = DriverManager.getConnection(url, "root", "");
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, cod_soc);
			pstmt.setString(2, nombre);
			pstmt.setString(3, apellidos);
			pstmt.setString(4, direccion);
			pstmt.setString(5, telefono);
			pstmt.setString(6, poblacion);
			pstmt.setString(7, cp);
			pstmt.setString(8, provincia);
			pstmt.setString(9, pais);
			pstmt.setInt(10, edad);
			pstmt.setDate(11, fecha_alta);
			pstmt.setInt(12, cuota);
			pstmt.executeUpdate();
			System.out.println("\nSoci donat d'alta correctament.");
			// select tabla socio
			pstmt = conn.prepareStatement("SELECT * FROM socio");
			ResultSet rs = pstmt.executeQuery();
			System.out.println("\n---Tabla socio---");
			while (rs.next()) {
				System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
						+ rs.getString(4) + "\t" + rs.getString(5) + "\t" + rs.getString(6) + "\t" + rs.getString(7)
						+ "\t" + rs.getString(8) + "\t" + rs.getString(9) + "\t" + rs.getInt(10) + "\t" + rs.getDate(11)
						+ "\t" + rs.getInt(12));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void modificarCuota() {
		Scanner lectorInt = new Scanner(System.in);
		Scanner lectorString = new Scanner(System.in);
		System.out.println("\nInserta COD SOC:");
		int cod_soc = lectorInt.nextInt();
		// select del soci y update de la cuota
		try {
			Connection conn = DriverManager.getConnection(url, "root", "");
			PreparedStatement pstmt = conn
					.prepareStatement("SELECT cod_soc, nombre, apellidos, cuota FROM socio WHERE cod_soc = ?");
			pstmt.setInt(1, cod_soc);
			ResultSet rs = pstmt.executeQuery();
			// si no hay datos
			if (rs.next() == false)
				System.out.println("\nNo existeix cap soci amb aquest cod soc.");
			else {
				System.out.println("\n---Dades del soci---");
				// mientras haya datos que los muestre
				do {
					System.out.println(
							rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getInt(4));
				} while (rs.next());
				// pregunta para confirmar si quieres hacer el update de cuota
				boolean b = false;
				while (b == false) {
					System.out.println("\nVols modificar la cuota (s/n)?");
					String resposta = lectorString.nextLine();
					if (resposta.equalsIgnoreCase("s")) {
						b = true;
						System.out.println("Inserta la nova cuota:");
						int cuota = lectorInt.nextInt();
						pstmt = conn.prepareStatement("UPDATE socio SET cuota = ? WHERE cod_soc = ?");
						pstmt.setInt(1, cuota);
						pstmt.setInt(2, cod_soc);
						pstmt.executeUpdate();
						System.out.println("\nCuota modificada correctament.");
						// select tabla socio
						pstmt = conn.prepareStatement("SELECT * FROM socio");
						rs = pstmt.executeQuery();
						System.out.println("\n---Tabla socio---");
						while (rs.next()) {
							System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
									+ rs.getString(4) + "\t" + rs.getString(5) + "\t" + rs.getString(6) + "\t"
									+ rs.getString(7) + "\t" + rs.getString(8) + "\t" + rs.getString(9) + "\t"
									+ rs.getInt(10) + "\t" + rs.getDate(11) + "\t" + rs.getInt(12));
						}
					}
					if (resposta.equalsIgnoreCase("n"))
						b = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void eliminarSoci() {
		Scanner lectorInt = new Scanner(System.in);
		Scanner lectorString = new Scanner(System.in);
		System.out.println("\nInserta COD SOC:");
		int cod_soc = lectorInt.nextInt();
		// select y delete del soci
		try {
			Connection conn = DriverManager.getConnection(url, "root", "");
			PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM socio WHERE cod_soc = ?");
			pstmt.setInt(1, cod_soc);
			ResultSet rs = pstmt.executeQuery();
			// si no hay datos
			if (rs.next() == false)
				System.out.println("\nNo existeix cap soci amb aquest cod soc.");
			else {
				System.out.println("\n---Dades del soci---");
				// mientras haya datos que los muestre
				do {
					System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
							+ rs.getString(4) + "\t" + rs.getString(5) + "\t" + rs.getString(6) + "\t" + rs.getString(7)
							+ "\t" + rs.getString(8) + "\t" + rs.getString(9) + "\t" + rs.getInt(10) + "\t"
							+ rs.getDate(11) + "\t" + rs.getInt(12));
				} while (rs.next());
				// pregunta para confirmar si quieres hacer el delete de soci
				boolean b = false;
				while (b == false) {
					System.out.println("\nVols eliminar el soci (s/n)?");
					String resposta = lectorString.nextLine();
					if (resposta.equalsIgnoreCase("s")) {
						b = true;
						pstmt = conn.prepareStatement("DELETE FROM socio WHERE cod_soc = ?");
						pstmt.setInt(1, cod_soc);
						pstmt.executeUpdate();
						System.out.println("\nSoci eliminat correctament.");
						// select tabla socio
						pstmt = conn.prepareStatement("SELECT * FROM socio");
						rs = pstmt.executeQuery();
						System.out.println("\n---Tabla socio---");
						while (rs.next()) {
							System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t"
									+ rs.getString(4) + "\t" + rs.getString(5) + "\t" + rs.getString(6) + "\t"
									+ rs.getString(7) + "\t" + rs.getString(8) + "\t" + rs.getString(9) + "\t"
									+ rs.getInt(10) + "\t" + rs.getDate(11) + "\t" + rs.getInt(12));
						}
					}
					if (resposta.equalsIgnoreCase("n"))
						b = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}