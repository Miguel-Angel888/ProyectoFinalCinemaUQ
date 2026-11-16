# CinemaUQ – Reportes y estadísticas

El administrador entra con `admin@cinemauq.com` / `admin` y pulsa **REPORTES Y GRÁFICOS**.

## Cómo se usa

1. Elegir el tipo de reporte en la lista de la izquierda.
2. Elegir el periodo con las fechas *Desde* / *Hasta* o con los atajos *7 días*, *30 días* y *90 días*.
3. **GENERAR REPORTE** muestra:
   - el gráfico de JavaFX (barras, pastel o líneas),
   - la tabla con los registros encontrados,
   - el panel de totales.
4. **Exportar TXT** / **Exportar PDF** guarda exactamente el mismo resultado en un archivo.

Si el periodo agrupa 62 días o menos, los gráficos por fecha muestran un punto por día; con
periodos más largos agrupan por mes.

## Reportes disponibles

| Reporte | Gráfico | Registros (filas) | Totales |
|---|---|---|---|
| Ventas por periodo | Líneas (ventas por día o mes) | Cada compra cobrada en el periodo | Compras, entradas, total vendido, total reembolsado, ingreso neto, ticket promedio |
| Entradas vendidas por película y función | Pastel (entradas por película) | Cada función con ventas | Películas, funciones, entradas vendidas, valor de las entradas |
| Ocupación de salas | Barras (% de ocupación por sala) | Cada función del periodo (sin las canceladas) | Funciones, asientos ocupados, capacidad ofrecida, ocupación promedio |
| Productos y combos vendidos | Barras (unidades por ítem) | Cada producto o combo vendido | Ítems distintos, unidades, unidades sueltas y en combo, total confitería |
| Recargas realizadas | Barras (total recargado por cliente) | Cada recarga de saldo en las tarjetas | Recargas, clientes, total recargado, recarga promedio |
| Cancelaciones y reembolsos | Pastel (cancelaciones por motivo) | Cada reembolso del periodo | Cancelaciones por política, valor pagado, total reembolsado, retenido por el cine |
| Puntos acumulados y redimidos | Barras (dos series: acumulados y redimidos) | Cada acumulación y cada canje | Acumulados, redimidos, diferencia, puntos vigentes hoy |

Las compras reembolsadas cuentan en *Ventas* (se cobraron) y en *Cancelaciones*, pero no en
*Entradas* ni en *Productos y combos*, porque esas entradas y productos se devolvieron.

## Contenido de los archivos exportados

Todo archivo TXT o PDF incluye:

- título del reporte,
- fecha y hora de generación,
- periodo consultado,
- número de registros encontrados,
- tabla de registros con columnas alineadas (montos y cantidades a la derecha),
- sección **TOTALES**.

## Estructura en el código

| Clase | Responsabilidad |
|---|---|
| `model/servicio/ServicioEstadisticas` | Calcula cada reporte a partir de los datos del cine y devuelve un `DatosReporte`. |
| `model/reporte/DatosReporte` | Resultado común: columnas, filas, totales y series para el gráfico. |
| `model/reporte/SerieGrafico`, `TipoGrafico`, `TipoReporte` | Datos y tipo de gráfico de cada reporte. |
| `model/servicio/ServicioReportes` | Exporta un `DatosReporte` con la fábrica del formato elegido. |
| `model/reporte/Reporte`, `ReporteTxt`, `ReportePdf` | Escriben el archivo (el PDF usa PDFBox). |
| `model/exceptions/ReporteInvalidoException` | Periodo inválido, tipo sin seleccionar o archivo que no se pudo guardar. |
| `controller/ReportesController` + `view/reportes.fxml` | Pantalla con `BarChart`, `PieChart` y `LineChart` de JavaFX. |

## Datos de ejemplo

`model/DatosHistoricos` genera, con una semilla fija, los últimos 90 días de operación:
funciones ya pasadas, compras con confitería, recargas, canjes de puntos, cancelaciones de
clientes (100 % u 80 % según la política) y una función cancelada por el cine. Así los reportes
tienen datos desde el primer arranque. Los usuarios de prueba son:

| Usuario | Contraseña | Notas |
|---|---|---|
| `admin@cinemauq.com` | `admin` | Administrador |
| `ana@cinemauq.com` | `1234` | Cliente con historial, saldo y puntos |
| `luis@cinemauq.com` | `1234` | Cliente sin historial y con $10.000 (para probar el pago rechazado) |
| `camila@`, `santiago@`, `valentina@`, `mateo@`, `laura@`, `julian@`, `sofia@`, `andres@cinemauq.com` | `1234` | Clientes del historial |

## Pruebas

```bash
cd proyectoFinalCinemaUQ
mvn clean test
```

Las pruebas del modelo están en `proyectoFinalCinemaUQ/src/test/java` y cubren el patrón State,
las políticas de reembolso (incluida la función cancelada por el cine), el Observer, la
coherencia del historial, el cálculo de cada reporte y la exportación a TXT y PDF.
