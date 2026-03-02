package com.realestate.controllers;

import com.realestate.models.Proveedor;
import com.realestate.services.ProveedorService;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  CONTROLADOR: ProveedorServlet                                           │
 * │  URL Pattern: /proveedores                                               │
 * │  Descripción: Recibe el formulario de registro de proveedores            │
 * │               desde dashboard_rh.html                                   │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * POST /proveedores → valida sesión, crea el proveedor y responde con HTML
 */
public class ProveedorServlet extends HttpServlet {

    private final ProveedorService proveedorService = new ProveedorService();

    // ─── POST: registrar nuevo proveedor ──────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        // 1. Verificar que el usuario tenga sesión activa con perfil RH_ADMIN
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("perfil") == null
                || !"RH_ADMIN".equals(session.getAttribute("perfil"))) {
            resp.sendRedirect(req.getContextPath() + "/login.html?error="
                + java.net.URLEncoder.encode("Sesión expirada. Por favor, inicia sesión nuevamente.", "UTF-8"));
            return;
        }

        // 2. Leer y validar parámetros del formulario
        String nombre         = req.getParameter("nombre");
        String rfc            = req.getParameter("rfc");
        String nombreContacto = req.getParameter("nombreContacto");
        String telefono       = req.getParameter("telefono");

        if (estaVacio(nombre) || estaVacio(rfc) || estaVacio(nombreContacto) || estaVacio(telefono)) {
            mostrarRespuestaHTML(resp, false,
                "Todos los campos son obligatorios. Por favor, completa el formulario.",
                null, req.getContextPath());
            return;
        }

        // 3. Construir objeto Proveedor y delegar al servicio
        Proveedor proveedor = new Proveedor(
            nombre.trim(),
            rfc.trim().toUpperCase(),
            nombreContacto.trim(),
            telefono.trim()
        );

        boolean guardado = proveedorService.registrarProveedor(proveedor);

        // 4. Devolver respuesta HTML con confirmación o error
        if (guardado) {
            mostrarRespuestaHTML(resp, true,
                "El proveedor fue registrado exitosamente.", proveedor, req.getContextPath());
        } else {
            mostrarRespuestaHTML(resp, false,
                "Ocurrió un error al registrar el proveedor. Intenta de nuevo.",
                null, req.getContextPath());
        }
    }

    // ─── Utilidades privadas ──────────────────────────────────────────────

    private boolean estaVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    /**
     * Genera la respuesta HTML de confirmación o error y la escribe en la respuesta.
     */
    private void mostrarRespuestaHTML(HttpServletResponse resp, boolean exito,
                                       String mensaje, Proveedor p, String contextPath)
            throws IOException {

        PrintWriter out = resp.getWriter();

        String colorFondo   = exito ? "#d1fae5" : "#fee2e2";
        String colorBorde   = exito ? "#10b981" : "#ef4444";
        String colorTexto   = exito ? "#065f46" : "#991b1b";
        String icono        = exito ? "✅" : "❌";

        out.println("<!DOCTYPE html>");
        out.println("<html lang='es'><head>");
        out.println("  <meta charset='UTF-8'>");
        out.println("  <meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("  <title>" + (exito ? "Registro Exitoso" : "Error") + " | RealEstate RH</title>");
        out.println("  <style>");
        out.println("    body { font-family: 'Segoe UI', sans-serif; background: #f0f4f8; display: flex; align-items: center; justify-content: center; min-height: 100vh; margin: 0; }");
        out.println("    .card { background: #fff; border-radius: 12px; padding: 40px; max-width: 520px; width: 90%; box-shadow: 0 4px 20px rgba(0,0,0,.12); text-align: center; }");
        out.println("    .alerta { background:" + colorFondo + "; border: 2px solid " + colorBorde + "; border-radius: 8px; padding: 18px; margin-bottom: 24px; color:" + colorTexto + "; font-weight: 600; }");
        out.println("    .detalle { background: #f8fafc; border-radius: 8px; padding: 16px; text-align: left; margin-bottom: 24px; font-size: .9rem; }");
        out.println("    .detalle span { font-weight: 700; color: #374151; }");
        out.println("    .btn { display: inline-block; padding: 12px 28px; border-radius: 8px; text-decoration: none; font-weight: 600; margin: 6px; }");
        out.println("    .btn-primary { background: #1e40af; color: #fff; }");
        out.println("    .btn-secondary { background: #e5e7eb; color: #374151; }");
        out.println("    h2 { color: #1e293b; margin-top: 0; }");
        out.println("  </style>");
        out.println("</head><body><div class='card'>");
        out.println("  <h2>" + icono + " " + (exito ? "Registro Exitoso" : "Error en el Registro") + "</h2>");
        out.println("  <div class='alerta'>" + mensaje + "</div>");

        if (exito && p != null) {
            out.println("  <div class='detalle'>");
            out.println("    <p><span>Constructora:</span> " + escapeHtml(p.getNombre()) + "</p>");
            out.println("    <p><span>RFC:</span> " + escapeHtml(p.getRfc()) + "</p>");
            out.println("    <p><span>Contacto:</span> " + escapeHtml(p.getNombreContacto()) + "</p>");
            out.println("    <p><span>Teléfono:</span> " + escapeHtml(p.getTelefono()) + "</p>");
            out.println("    <p><span>ID asignado:</span> #" + p.getId() + "</p>");
            out.println("  </div>");
        }

        out.println("  <a href='" + contextPath + "/dashboard_rh.html' class='btn btn-primary'>Registrar otro proveedor</a>");
        out.println("  <a href='" + contextPath + "/login' class='btn btn-secondary'>Cerrar sesión</a>");
        out.println("</div></body></html>");
    }

    /** Escapa caracteres HTML para prevenir XSS en la respuesta. */
    private String escapeHtml(String texto) {
        if (texto == null) return "";
        return texto.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }
}
