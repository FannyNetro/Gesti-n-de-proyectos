package com.realestate.controllers;

import com.realestate.models.Empleado;
import com.realestate.services.EmpleadoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * ┌─────────────────────────────────────────────────────────────────────────┐
 * │  CONTROLADOR: EmpleadoServlet                                            │
 * │  URL Pattern: /empleados  (declarado en web.xml)                         │
 * │                                                                          │
 * │  POST /empleados → recibe el formulario de alta de empleado de RH        │
 * │                    valida sesión, construye el objeto Empleado,           │
 * │                    llama al servicio y devuelve página de confirmación.   │
 * └─────────────────────────────────────────────────────────────────────────┘
 *
 * @MultipartConfig es requerido para poder leer el campo de tipo "file"
 * (la foto) con request.getPart("foto").
 *
 * FUTURO JDBC:  Ver EmpleadoService.java — el INSERT a SQL Server ya está
 *               preparado y comentado, listo para descomentar.
 */
@MultipartConfig(
    maxFileSize    = 5 * 1024 * 1024,   // 5 MB por archivo
    maxRequestSize = 10 * 1024 * 1024   // 10 MB total del request
)
public class EmpleadoServlet extends HttpServlet {

    private final EmpleadoService empleadoService = new EmpleadoService();

    /** POST /empleados → procesar formulario de registro de empleado */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        // 1. Verificar sesión activa con perfil RH_ADMIN
        HttpSession session = req.getSession(false);
        if (session == null || !"RH_ADMIN".equals(session.getAttribute("perfil"))) {
            resp.sendRedirect(req.getContextPath() + "/login.html?error="
                + java.net.URLEncoder.encode("Sesión expirada. Inicia sesión nuevamente.", "UTF-8"));
            return;
        }

        // 2. Leer parámetros del formulario
        String nombre    = req.getParameter("nombreCompleto");
        String direccion = req.getParameter("direccion");
        String telefono  = req.getParameter("telefono");
        String puesto    = req.getParameter("puesto");
        String sueldo    = req.getParameter("sueldo");

        // 3. Validar campos obligatorios
        if (estaVacio(nombre) || estaVacio(direccion) || estaVacio(telefono)
                || estaVacio(puesto) || estaVacio(sueldo)) {
            mostrarRespuesta(resp, false,
                "Todos los campos son obligatorios.", null, req.getContextPath());
            return;
        }

        // 4. Leer el archivo de foto (campo multipart)
        String nombreFoto = null;
        try {
            Part fotoPart = req.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                // Extraer nombre del archivo enviado
                String header = fotoPart.getHeader("content-disposition");
                if (header != null) {
                    for (String token : header.split(";")) {
                        if (token.trim().startsWith("filename")) {
                            nombreFoto = token.substring(token.indexOf('=') + 1)
                                             .trim().replace("\"", "");
                        }
                    }
                }

                /* ══════════════════════════════════════════════════════════
                 *  FUTURO: Guardar el archivo físicamente en el servidor
                 * ══════════════════════════════════════════════════════════
                 *  String uploadDir = getServletContext().getRealPath("/uploads/fotos");
                 *  new File(uploadDir).mkdirs();
                 *  fotoPart.write(uploadDir + File.separator + nombreFoto);
                 *
                 *  // También guardar la ruta relativa en la BD:
                 *  // empleado.setNombreFoto("/uploads/fotos/" + nombreFoto);
                 * ══════════════════════════════════════════════════════════ */
            }
        } catch (Exception ex) {
            // Si falla la lectura del archivo no bloqueamos el registro
            System.err.println("[EmpleadoServlet] Advertencia al leer foto: " + ex.getMessage());
        }

        // 5. Construir objeto Empleado y delegar al servicio
        Empleado empleado = new Empleado(
            nombre.trim(),
            direccion.trim(),
            telefono.trim(),
            puesto.trim(),
            sueldo.trim(),
            nombreFoto
        );

        String errorMsg = empleadoService.registrarEmpleado(empleado);

        // 6. Responder con HTML de confirmación
        if (errorMsg == null) {
            mostrarRespuesta(resp, true,
                "Empleado registrado correctamente.", empleado, req.getContextPath());
        } else {
            mostrarRespuesta(resp, false,
                "Error al registrar: " + errorMsg, null, req.getContextPath());
        }
    }

    // ── Páginas HTML de respuesta ─────────────────────────────────────────

    private void mostrarRespuesta(HttpServletResponse resp, boolean exito,
                                   String mensaje, Empleado e, String ctx)
            throws IOException {

        PrintWriter out = resp.getWriter();

        String colorFondo = exito ? "#f0fdf4" : "#fef3f2";
        String colorBorde = exito ? "#86efac" : "#f4a79d";
        String colorTexto = exito ? "#166534" : "#b42318";
        String icono      = exito ? "✅" : "❌";

        out.println("<!DOCTYPE html><html lang='es'><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width,initial-scale=1'>");
        out.println("<title>" + (exito ? "Registro Exitoso" : "Error") + " | VG Tech RH</title>");
        out.println("<style>");
        out.println("*{box-sizing:border-box;margin:0;padding:0}");
        out.println("body{font-family:'Segoe UI',system-ui,sans-serif;background:#f0f2f5;");
        out.println("  display:flex;align-items:center;justify-content:center;min-height:100vh;padding:24px;}");
        out.println(".card{background:#fff;border:1px solid #dbe1ea;border-radius:14px;");
        out.println("  padding:40px 36px;max-width:520px;width:100%;");
        out.println("  box-shadow:0 8px 32px rgba(13,31,60,.12);text-align:center;}");
        out.println(".card::before{content:'';display:block;width:44px;height:3px;");
        out.println("  background:#c9a84c;border-radius:99px;margin:0 auto 22px;}");
        out.println(".brand{font-size:1.3rem;font-weight:800;color:#0d1f3c;margin-bottom:20px;}");
        out.println(".brand em{color:#3d8a85;font-style:normal;}");
        out.println(".alerta{border-radius:8px;padding:14px 16px;margin-bottom:20px;");
        out.println("  border:1.5px solid " + colorBorde + ";background:" + colorFondo + ";");
        out.println("  color:" + colorTexto + ";font-weight:600;font-size:.95rem;}");
        out.println(".detalle{background:#f8fafc;border-radius:8px;padding:16px;");
        out.println("  text-align:left;margin-bottom:24px;font-size:.88rem;line-height:1.7;}");
        out.println(".detalle strong{color:#1a2b45;}");
        out.println(".id-badge{display:inline-block;background:#0d1f3c;color:#fff;");
        out.println("  border-radius:99px;padding:2px 12px;font-size:.82rem;font-weight:700;margin-bottom:16px;}");
        out.println(".btn{display:inline-block;padding:12px 26px;border-radius:8px;");
        out.println("  text-decoration:none;font-weight:700;margin:6px;font-size:.92rem;}");
        out.println(".btn-teal{background:#3d8a85;color:#fff;}");
        out.println(".btn-grey{background:#e5e7eb;color:#374151;}");
        out.println("</style></head><body><div class='card'>");

        out.println("<div class='brand'>VG<em> Tech</em></div>");
        out.println("<h2 style='font-size:1.25rem;color:#0d1f3c;margin-bottom:16px;'>");
        out.println("  " + icono + " " + (exito ? "Registro Exitoso" : "Error en el Registro") + "</h2>");
        out.println("<div class='alerta'>" + esc(mensaje) + "</div>");

        if (exito && e != null) {
            out.println("<div class='id-badge'>ID Empleado #" + e.getId() + "</div>");
            out.println("<div class='detalle'>");
            out.println("  <p><strong>Nombre:</strong> " + esc(e.getNombreCompleto()) + "</p>");
            out.println("  <p><strong>Dirección:</strong> " + esc(e.getDireccion()) + "</p>");
            out.println("  <p><strong>Teléfono:</strong> " + esc(e.getTelefono()) + "</p>");
            out.println("  <p><strong>Puesto:</strong> " + esc(e.getPuesto()) + "</p>");
            out.println("  <p><strong>Sueldo:</strong> $" + esc(e.getSueldo()) + " MXN</p>");
            if (e.getNombreFoto() != null && !e.getNombreFoto().isBlank()) {
                out.println("  <p><strong>Foto:</strong> " + esc(e.getNombreFoto()) + "</p>");
            }
            out.println("</div>");
        }

        out.println("<a href='" + ctx + "/dashboard_rh.html' class='btn btn-teal'>Registrar otro empleado</a>");
        out.println("<a href='" + ctx + "/login' class='btn btn-grey'>Cerrar sesión</a>");
        out.println("</div></body></html>");
    }

    private boolean estaVacio(String v) {
        return v == null || v.isBlank();
    }

    /** Escapa caracteres HTML para prevenir XSS */
    private String esc(String t) {
        if (t == null) return "";
        return t.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
}
