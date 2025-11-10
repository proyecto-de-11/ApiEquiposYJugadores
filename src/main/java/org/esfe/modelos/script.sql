CREATE DATABASE IF NOT EXISTS `EquiposYJugdoresBD`;
USE `EquiposYJugdoresBD`;

CREATE TABLE IF NOT EXISTS tipos_deporte (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL UNIQUE, -- 'Fútbol', 'Basketball', etc.
    descripcion TEXT,
    icono VARCHAR(500),
    esta_activo BOOLEAN DEFAULT TRUE
) ENGINE = InnoDB;

-- 2. TABLA PRINCIPAL: Equipos
CREATE TABLE IF NOT EXISTS equipos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    creado_por INT NOT NULL, -- FK a usuarios(id)
    tipo_deporte_id INT NOT NULL, -- FK a tipos_deporte(id)
    descripcion TEXT,
    logo VARCHAR(500),
    color_principal VARCHAR(7),
    color_secundario VARCHAR(7),
    ciudad VARCHAR(100),
    nivel ENUM('principiante', 'intermedio', 'avanzado', 'profesional'),
    max_miembros INT DEFAULT 15,
    requiere_aprobacion BOOLEAN DEFAULT TRUE,
    calificacion_promedio DECIMAL(3, 2) DEFAULT 0,
    total_calificaciones INT DEFAULT 0,
    esta_activo BOOLEAN DEFAULT TRUE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (creado_por) REFERENCES usuarios(id),
    FOREIGN KEY (tipo_deporte_id) REFERENCES tipos_deporte(id)
) ENGINE = InnoDB;

-- 3. TABLA DE RELACIÓN: Miembros del Equipo
CREATE TABLE IF NOT EXISTS equipo_miembros (
    id INT PRIMARY KEY AUTO_INCREMENT,
    equipo_id INT NOT NULL, -- FK a equipos(id)
    usuario_id INT NOT NULL, -- FK a usuarios(id)
    rol VARCHAR(50) DEFAULT 'jugador', -- ENUM('capitan', 'vice_capitan', 'jugador')
    numero_camiseta INT,
    posicion VARCHAR(100),
    estado VARCHAR(50) DEFAULT 'activo', -- ENUM('activo', 'inactivo', 'suspendido')
    fecha_union DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipo_id) REFERENCES equipos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    UNIQUE KEY unique_miembro (equipo_id, usuario_id)
) ENGINE = InnoDB;

-- 4. TABLA DE RELACIÓN: Invitaciones a Equipos
CREATE TABLE IF NOT EXISTS invitaciones_equipo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    equipo_id INT NOT NULL, -- FK a equipos(id)
    usuario_invitado_id INT NOT NULL, -- FK a usuarios(id)
    usuario_remitente_id INT NOT NULL, -- FK a usuarios(id)
    usuario_respondio_id INT, -- FK a usuarios(id)
    mensaje TEXT,
    estado VARCHAR(50) DEFAULT 'pendiente', -- ENUM('pendiente', 'aceptada', 'rechazada', 'cancelada')
    fecha_respuesta DATETIME NULL,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (equipo_id) REFERENCES equipos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_invitado_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_remitente_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_respondio_id) REFERENCES usuarios(id)
) ENGINE = InnoDB;

-- 5. TABLA DE ESTADÍSTICAS: Estadísticas del Equipo
CREATE TABLE IF NOT EXISTS estadisticas_equipo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    equipo_id INT NOT NULL UNIQUE, -- FK a equipos(id)
    partidos_jugados_total INT DEFAULT 0,
    partidos_ganados_total INT DEFAULT 0,
    partidos_perdidos_total INT DEFAULT 0,
    partidos_empatados_total INT DEFAULT 0,
    goles_favor_total INT DEFAULT 0,
    goles_contra_total INT DEFAULT 0,
    partidos_jugados_torneos INT DEFAULT 0,
    partidos_ganados_torneos INT DEFAULT 0,
    partidos_perdidos_torneos INT DEFAULT 0,
    partidos_empatados_torneos INT DEFAULT 0,
    goles_favor_torneos INT DEFAULT 0,
    goles_contra_torneos INT DEFAULT 0,
    torneos_ganados INT DEFAULT 0,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (equipo_id) REFERENCES equipos(id) ON DELETE CASCADE
) ENGINE = InnoDB;

-- 6. TABLA DE CALIFICACIONES: Calificaciones al Equipo
-- Esta tabla requiere una dependencia a 'partidos' que debe existir en otra API,
-- pero se puede crear sin esa FK por ahora.
CREATE TABLE IF NOT EXISTS calificaciones_equipo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    partido_id INT, -- FK a partidos(id) (opcional si es otra API)
    evaluador_id INT NOT NULL, -- FK a usuarios(id)
    equipo_evaluado_id INT NOT NULL, -- FK a equipos(id)
    puntuacion DECIMAL(2, 1),
    aspectos_positivos JSON,
    aspectos_mejorar JSON,
    comentario TEXT,
    es_anonimo BOOLEAN DEFAULT FALSE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (evaluador_id) REFERENCES usuarios(id),
    FOREIGN KEY (equipo_evaluado_id) REFERENCES equipos(id),
    -- UNIQUE KEY unique_calificacion (partido_id, evaluador_id, equipo_evaluado_id)
    -- Se elimina el UNIQUE KEY que usa partido_id, por si la tabla partidos aún no existe
) ENGINE = InnoDB;