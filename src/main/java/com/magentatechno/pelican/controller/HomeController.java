package com.magentatechno.pelican.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller pour les endpoints publics (sans authentification)
 */
@RestController
@RequestMapping("/public")
public class HomeController {

    /**
     * Page d'accueil HTML
     */
    @GetMapping(value = "", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> home() {
        String html = generateHomePage();
        return ResponseEntity.ok(html);
    }

    /**
     * Informations API en JSON
     */
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "🦩 Pelican by MagentaTechno");
        response.put("version", "1.0.0");
        response.put("description", "Système de Gestion Électronique du Courrier");
        response.put("status", "✅ Running");
        response.put("timestamp", LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        Map<String, String> links = new HashMap<>();
        links.put("documentation", "/api/v1/swagger-ui/index.html");
        links.put("api_docs", "/api/v1/v3/api-docs");
        links.put("health", "/api/v1/public/health");
        links.put("login", "/api/v1/auth/login");
        response.put("links", links);
        
        Map<String, String> contact = new HashMap<>();
        contact.put("company", "MagentaTechno");
        contact.put("country", "Sénégal");
        contact.put("email", "contact@magentatechno.sn");
        response.put("contact", contact);
        
        return response;
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("application", "Pelican Backend");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        return response;
    }

    /**
     * Génère le HTML de la page d'accueil
     */
    private String generateHomePage() {
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>🦩 Pelican by MagentaTechno</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, sans-serif;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        padding: 20px;
                    }
                    
                    .container {
                        background: white;
                        border-radius: 20px;
                        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                        max-width: 800px;
                        width: 100%;
                        overflow: hidden;
                    }
                    
                    .header {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                        padding: 40px;
                        text-align: center;
                    }
                    
                    .logo {
                        font-size: 80px;
                        margin-bottom: 10px;
                        animation: bounce 2s infinite;
                    }
                    
                    @keyframes bounce {
                        0%, 100% { transform: translateY(0); }
                        50% { transform: translateY(-20px); }
                    }
                    
                    h1 {
                        font-size: 36px;
                        margin-bottom: 10px;
                    }
                    
                    .subtitle {
                        font-size: 16px;
                        opacity: 0.9;
                    }
                    
                    .badge {
                        display: inline-block;
                        background: rgba(255, 255, 255, 0.2);
                        padding: 5px 15px;
                        border-radius: 20px;
                        font-size: 14px;
                        margin-top: 15px;
                    }
                    
                    .content {
                        padding: 40px;
                    }
                    
                    .description {
                        color: #555;
                        line-height: 1.6;
                        margin-bottom: 30px;
                        text-align: center;
                        font-size: 16px;
                    }
                    
                    .features {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                        gap: 15px;
                        margin-bottom: 30px;
                    }
                    
                    .feature {
                        background: #f8f9fa;
                        padding: 15px;
                        border-radius: 10px;
                        text-align: center;
                        transition: transform 0.3s;
                    }
                    
                    .feature:hover {
                        transform: translateY(-5px);
                        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
                    }
                    
                    .feature-icon {
                        font-size: 32px;
                        margin-bottom: 10px;
                    }
                    
                    .feature-title {
                        font-weight: bold;
                        color: #333;
                        margin-bottom: 5px;
                    }
                    
                    .feature-desc {
                        font-size: 13px;
                        color: #666;
                    }
                    
                    .actions {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
                        gap: 15px;
                        margin-bottom: 30px;
                    }
                    
                    .btn {
                        display: block;
                        padding: 15px 25px;
                        text-align: center;
                        border-radius: 10px;
                        text-decoration: none;
                        font-weight: bold;
                        transition: all 0.3s;
                        cursor: pointer;
                    }
                    
                    .btn-primary {
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        color: white;
                    }
                    
                    .btn-primary:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                    }
                    
                    .btn-secondary {
                        background: #f8f9fa;
                        color: #333;
                        border: 2px solid #e9ecef;
                    }
                    
                    .btn-secondary:hover {
                        background: #e9ecef;
                        transform: translateY(-2px);
                    }
                    
                    .info {
                        background: #f0f4ff;
                        border-left: 4px solid #667eea;
                        padding: 15px 20px;
                        border-radius: 5px;
                        margin-bottom: 20px;
                    }
                    
                    .info-title {
                        font-weight: bold;
                        color: #333;
                        margin-bottom: 5px;
                    }
                    
                    .info-text {
                        color: #666;
                        font-size: 14px;
                    }
                    
                    .status {
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        gap: 10px;
                        padding: 10px;
                        background: #d4edda;
                        color: #155724;
                        border-radius: 10px;
                        font-weight: bold;
                        margin-bottom: 20px;
                    }
                    
                    .status-dot {
                        width: 12px;
                        height: 12px;
                        background: #28a745;
                        border-radius: 50%;
                        animation: pulse 2s infinite;
                    }
                    
                    @keyframes pulse {
                        0%, 100% { opacity: 1; }
                        50% { opacity: 0.5; }
                    }
                    
                    .footer {
                        background: #f8f9fa;
                        padding: 20px;
                        text-align: center;
                        color: #666;
                        font-size: 14px;
                    }
                    
                    .footer a {
                        color: #667eea;
                        text-decoration: none;
                    }
                    
                    .endpoints {
                        background: #1e1e1e;
                        color: #fff;
                        padding: 20px;
                        border-radius: 10px;
                        font-family: 'Courier New', monospace;
                        font-size: 13px;
                        margin-bottom: 20px;
                        overflow-x: auto;
                    }
                    
                    .endpoints h3 {
                        color: #61dafb;
                        margin-bottom: 10px;
                    }
                    
                    .endpoint {
                        margin: 5px 0;
                        color: #d4d4d4;
                    }
                    
                    .method {
                        display: inline-block;
                        padding: 2px 8px;
                        border-radius: 3px;
                        font-weight: bold;
                        margin-right: 10px;
                        font-size: 11px;
                    }
                    
                    .method-get { background: #61dafb; color: #000; }
                    .method-post { background: #49cc90; color: #000; }
                    .method-put { background: #fca130; color: #000; }
                    .method-delete { background: #f93e3e; color: #fff; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🦩</div>
                        <h1>Pelican</h1>
                        <p class="subtitle">by MagentaTechno</p>
                        <div class="badge">v1.0.0 • Production Ready</div>
                    </div>
                    
                    <div class="content">
                        <div class="status">
                            <div class="status-dot"></div>
                            API Online & Operational
                        </div>
                        
                        <p class="description">
                            🚀 Système de Gestion Électronique du Courrier (GEC) <br>
                            Une solution moderne pour digitaliser la gestion du courrier au Sénégal
                        </p>
                        
                        <div class="features">
                            <div class="feature">
                                <div class="feature-icon">📨</div>
                                <div class="feature-title">Courriers</div>
                                <div class="feature-desc">Gestion entrants/sortants</div>
                            </div>
                            <div class="feature">
                                <div class="feature-icon">🔄</div>
                                <div class="feature-title">Workflow</div>
                                <div class="feature-desc">Circuit de validation</div>
                            </div>
                            <div class="feature">
                                <div class="feature-icon">🔐</div>
                                <div class="feature-title">Sécurité</div>
                                <div class="feature-desc">JWT + RBAC</div>
                            </div>
                            <div class="feature">
                                <div class="feature-icon">📊</div>
                                <div class="feature-title">Dashboard</div>
                                <div class="feature-desc">Statistiques temps réel</div>
                            </div>
                            <div class="feature">
                                <div class="feature-icon">📄</div>
                                <div class="feature-title">PDF</div>
                                <div class="feature-desc">Génération automatique</div>
                            </div>
                            <div class="feature">
                                <div class="feature-icon">🔔</div>
                                <div class="feature-title">Notifications</div>
                                <div class="feature-desc">Alertes en temps réel</div>
                            </div>
                        </div>
                        
                        <div class="info">
                            <div class="info-title">📋 Pour les Développeurs Frontend</div>
                            <div class="info-text">
                                Utilisez la documentation Swagger pour explorer tous les endpoints de l'API.
                                Le token JWT est requis pour la plupart des endpoints sauf /auth/** et /public/**.
                            </div>
                        </div>
                        
                        <div class="actions">
                            <a href="/api/v1/swagger-ui/index.html" class="btn btn-primary">
                                📚 Documentation API
                            </a>
                            <a href="/api/v1/public/info" class="btn btn-secondary">
                                ℹ️ Infos API
                            </a>
                            <a href="/api/v1/public/health" class="btn btn-secondary">
                                💚 Health Check
                            </a>
                            <a href="/api/v1/v3/api-docs" class="btn btn-secondary">
                                📖 OpenAPI JSON
                            </a>
                        </div>
                        
                        <div class="endpoints">
                            <h3>🔌 Endpoints Principaux</h3>
                            <div class="endpoint">
                                <span class="method method-post">POST</span>
                                /api/v1/auth/login - Connexion
                            </div>
                            <div class="endpoint">
                                <span class="method method-get">GET</span>
                                /api/v1/users/me - Mon profil
                            </div>
                            <div class="endpoint">
                                <span class="method method-get">GET</span>
                                /api/v1/courriers - Liste des courriers
                            </div>
                            <div class="endpoint">
                                <span class="method method-post">POST</span>
                                /api/v1/courriers - Créer un courrier
                            </div>
                            <div class="endpoint">
                                <span class="method method-get">GET</span>
                                /api/v1/dashboard/statistics - Statistiques
                            </div>
                            <div class="endpoint">
                                <span class="method method-get">GET</span>
                                /api/v1/notifications - Notifications
                            </div>
                            <div class="endpoint">
                                <span class="method method-get">GET</span>
                                /api/v1/courriers/{id}/pdf - Télécharger PDF
                            </div>
                        </div>
                        
                        <div class="info">
                            <div class="info-title">🔑 Comptes de Test</div>
                            <div class="info-text">
                                <strong>Admin:</strong> admin@pelican.sn / Admin123!<br>
                                <strong>Agent:</strong> agent@pelican.sn / Agent123!
                            </div>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>
                            🦩 Pelican by MagentaTechno © 2026 • 
                            <a href="/api/v1/swagger-ui/index.html">Documentation</a> • 
                            <a href="/api/v1/public/health">Status</a>
                        </p>
                        <p style="margin-top: 5px;">
                            Made with ❤️ in Senegal 🇸🇳
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }
}
