# Cuídame - Aplicación móvil para asistencia y cuidado de personas con discapacidad

![Android](https://img.shields.io/badge/Plataforma-Android-brightgreen)
![Firebase](https://img.shields.io/badge/Backend-Firebase-blue)
![Estado](https://img.shields.io/badge/Estado-En%20Desarrollo-yellow)

## 📱 Descripción general

**Cuídame** es una aplicación móvil desarrollada en Android, pensada para brindar apoyo a personas con discapacidad y facilitar el seguimiento remoto por parte de sus cuidadores. Integra funcionalidades críticas como recordatorios de medicamentos, envío de alertas de emergencia y ubicación segura. Todo dentro de una interfaz accesible, intuitiva y centrada en el bienestar del usuario.

---

## 🎯 Objetivos

- Garantizar la correcta toma de medicamentos mediante alarmas configurables.
- Permitir el envío rápido de alertas de emergencia al cuidador.
- Compartir la ubicación del usuario de forma segura.
- Facilitar la interacción entre cuidador y usuario sin necesidad de estar presentes.
- Mejorar la autonomía de personas con discapacidad dentro de un entorno controlado.

---

## ⚙️ Tecnologías utilizadas

| Componente     | Tecnología                        |
|----------------|-----------------------------------|
| Lenguaje       | Kotlin                            |
| IDE            | Android Studio                    |
| Base de datos  | Firebase Realtime Database        |
| Autenticación  | Firebase Authentication           |
| Alarmas        | AlarmManager + BroadcastReceiver  |
| Mapas          | Intents con Google Maps           |
| UI             | XML + Material Design             |

---


## 🔐 Seguridad

- Reglas de acceso en Firebase configuradas para restringir escritura/lectura solo a usuarios autenticados.
- Las alertas, ubicaciones y registros están asociados a UIDs únicos por usuario.
- Firebase Authentication protege el acceso con email y contraseña.

---

## 📈 Impacto social

- Interfaz adaptada a personas con discapacidad (botones grandes, navegación sencilla).
- Permite a cuidadores gestionar usuarios de forma remota.
- Apoyo real para personas mayores, con movilidad reducida o enfermedades crónicas.
- Potencial de uso institucional en hogares geriátricos o centros de atención.

---

## 🧪 Estado actual

| Funcionalidad            | Estado       |
|--------------------------|--------------|
| Registro / Login         | ✅ Completo   |
| Gestión de medicamentos  | ✅ Completo   |
| Alarmas de medicación    | ✅ Completo   |
| Envío de alertas         | ✅ Completo   |
| Visualización cuidador   | ✅ Completo   |
| Módulo ubicación segura  | ✅ Completo   |
| Panel cuidador con menú  | ✅ Completo   |
| Personalización y diseño | 🔄 En curso   |

---

## 🚀 Cómo ejecutar el proyecto

1. Clona este repositorio:
```bash
git clone https://github.com/tu-usuario/Cuidame-App.git
```

2. Abre el proyecto con Android Studio.

3. Conecta un dispositivo o crea un emulador.

4. Ejecuta el proyecto con el botón ▶️ (Run).


## 🤝 Autor 
**Sebastián Ibarra** – Desarrollador de la aplicación

---

## 📄 Licencia

Proyecto con fines académicos y sociales. Para contribuciones o usos externos, contactar al autor.

