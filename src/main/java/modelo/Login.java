/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import controlador.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Usuario
 */
public class Login {

	private final int id;
	private final String usuario;
	private final String rol;
	private final String tipo;
	private final Integer recursoId;

	public Login(int id, String usuario, String rol, String tipo, Integer recursoId) {
		this.id = id;
		this.usuario = usuario;
		this.rol = rol;
		this.tipo = tipo;
		this.recursoId = recursoId;
	}

	public static Login autenticar(String usuario, String contrasena) throws Exception {
		if (usuario == null || usuario.trim().isEmpty() || contrasena == null || contrasena.isEmpty())
			throw new Exception("Usuario y contrasena son obligatorios.");
		String sql = "SELECT id, usuario, rol, tipo, recurso_id FROM usuario WHERE usuario=? AND contrasena=? AND estado='Activo'";
		try (Connection conn = new ConexionBDD().conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, usuario.trim());
			stmt.setString(2, contrasena);
			try (ResultSet rs = stmt.executeQuery()) {
				if (!rs.next()) throw new Exception("Usuario o contrasena incorrectos.");
				Integer recursoId = (Integer) rs.getObject("recurso_id");
				if ("Gestor".equals(rs.getString("tipo")) && recursoId != null)
					throw new Exception("La cuenta gestora no puede estar asociada a un recurso.");
				if ("Recurso".equals(rs.getString("tipo")) && recursoId == null)
					throw new Exception("La cuenta de recurso no tiene un recurso asociado.");
				return new Login(rs.getInt("id"), rs.getString("usuario"),
						rs.getString("rol"), rs.getString("tipo"), recursoId);
			}
		}
	}

	   public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getRol() {
        return rol;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getRecursoId() {
        return recursoId;
    }
}
