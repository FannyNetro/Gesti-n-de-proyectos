package com.realestate.controllers;

import com.realestate.services.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  CONTROLADOR: LoginServlet                                               │
 * │  URL Pattern: /login  (declarado en web.xml)                             │
 * │  GET  → redirige a login.html                                            │
 * │  POST → valida credenciales del Admin RH y crea sesión                  │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * Credenciales simuladas (prototipo): admin_rh / 1234
 * La lógica de validación se delega a AuthService.java.
 */
public class LoginServlet extends HttpServlet {

    private final AuthService authService = new AuthService();

    /** GET /login → redirigir a la vista login.html */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/login.html");
    }

    /** POST /login → procesar credenciales del formulario */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String usuario  = req.getParameter("usuario");
        String password = req.getParameter("password");

        // Validar campos vacíos
        if (estaVacio(usuario) || estaVacio(password)) {
            redirigirConError(req, resp, "Por favor ingresa usuario y contraseña.");
            return;
        }

        // Delegar validación al servicio
        boolean acceso = authService.esAdminRH(usuario, password);

        if (acceso) {
            // Crear sesión y redirigir al dashboard de RH
            HttpSession session = req.getSession(true);
            session.setAttribute("usuarioLogueado", usuario.trim());
            session.setAttribute("perfil", "RH_ADMIN");
            resp.sendRedirect(req.getContextPath() + "/dashboard_rh.html");
        } else {
            redirigirConError(req, resp, "Credenciales incorrectas. Acceso denegado.");
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────

    private boolean estaVacio(String v) {
        return v == null || v.isBlank();
    }

    private void redirigirConError(HttpServletRequest req, HttpServletResponse resp,
                                    String mensaje) throws IOException {
        resp.sendRedirect(req.getContextPath()
            + "/login.html?error="
            + java.net.URLEncoder.encode(mensaje, "UTF-8"));
    }
}
