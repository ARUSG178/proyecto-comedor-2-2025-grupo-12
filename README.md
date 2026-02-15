# Sistema de Asignación y Gestión del Comedor (SAGC) - UCV

## Descripción
Sistema de gestión para el comedor universitario de la UCV, diseñado para administrar el registro de usuarios, control de acceso, gestión de menús y reservas.

## Tecnologías y Librerías

Este proyecto ha sido desarrollado utilizando **Java** (JDK 8+) como lenguaje base.

### Interfaz Gráfica (GUI)
De acuerdo con los requisitos del proyecto, la interfaz gráfica ha sido construida utilizando **Java Swing**.

**Aclaratoria sobre el uso de Librerías:**
Queremos hacer constar que para el desarrollo de este software:

1.  **Uso de Estándares:** Se han utilizado exclusivamente las librerías estándar incluidas en el JDK (`javax.swing`, `java.awt`, `java.io`, `java.nio`, `java.util`).
2.  **Personalización Visual:** Los componentes visuales con diseño moderno (bordes redondeados, sombras, transparencias y campos de texto estilizados) **no provienen de librerías externas** (como FlatLaf o JGoodies). Han sido implementados manualmente mediante la sobrescritura de métodos de renderizado (`paintComponent`) y el uso de la API de gráficos 2D de Java (`Graphics2D`).
3.  **Dependencias:** No se requieren archivos `.jar` de terceros ni gestores de dependencias externos para compilar o ejecutar la aplicación.

## Ejecución

El punto de entrada de la aplicación es la clase `Main`.

```bash
java com.comedor.Main
```

---
*Proyecto desarrollado por el Grupo 12 para la asignatura de Programación.*