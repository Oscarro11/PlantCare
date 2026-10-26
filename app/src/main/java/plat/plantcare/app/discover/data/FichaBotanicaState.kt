package plat.plantcare.app.discover.data

import androidx.annotation.DrawableRes

data class PlantaDetalle(
    @DrawableRes val imagen: Int? = null,
    val nombre: String,
    val nombreCientifico: String,
    val familia: String,
    val origen: String,
    val descripcion: String,
    val compatibilidad: Int
)

data class CaracteristicaPlanta(
    val icono: String,
    val titulo: String,
    val valor: String,
    val descripcion: String,
    val advertencia: Boolean = false
)

data class PasoCuidado(
    val titulo: String,
    val descripcion: String
)

data class FichaBotanicaState(
    val planta: PlantaDetalle = PlantaDetalle(
        imagen = null,
        nombre = "Zamioculcas",
        nombreCientifico = "Zamioculcas zamiifolia (Planta ZZ)",
        familia = "Araceae",
        origen = "África Oriental",
        compatibilidad = 95,
        descripcion = "Nativa de sabanas y bosques secos del este de " +
                "África, la Zamioculcas es célebre por su resistencia casi " +
                "indestructible. Sus hojas verde esmeralda con brillo encerado " +
                "nacen de rizomas subterráneos gruesos capaces de almacenar " +
                "agua durante meses."
    ),

    val caracteristicas: List<CaracteristicaPlanta> = listOf(
        CaracteristicaPlanta(
            icono = "☀",
            titulo = "LUZ",
            valor = "Indirecta / Sombra",
            descripcion = "Tolera pasillos y luz tenue; evitar sol directo."
        ),
        CaracteristicaPlanta(
            icono = "♧",
            titulo = "RIEGO",
            valor = "Cada 15–20 días",
            descripcion = "Sustrato totalmente seco antes de volver a regar."
        ),
        CaracteristicaPlanta(
            icono = "◉",
            titulo = "HUMEDAD",
            valor = "Baja o media",
            descripcion = "40% a 50%. No requiere pulverización regular."
        ),
        CaracteristicaPlanta(
            icono = "♣",
            titulo = "MASCOTAS",
            valor = "Precaución leve",
            descripcion = "Oxalatos cálcicos. Elevar fuera de su alcance.",
            advertencia = true
        )
    ),

    val pasos: List<PasoCuidado> = listOf(
        PasoCuidado(
            titulo = "Ubicación zen",
            descripcion = "Ubícala a 2–3 metros de cualquier ventana o " +
                    "en áreas con luz fluorescente de oficina. Si notas que " +
                    "los tallos se alargan buscando luz, muévela ligeramente " +
                    "más cerca."
        ),
        PasoCuidado(
            titulo = "La regla del menos es más",
            descripcion = "Sus rizomas retienen reservas; es preferible " +
                    "olvidar regarla que regar de más. En invierno basta una " +
                    "vez al mes. Drenar siempre el plato sobrante."
        ),
        PasoCuidado(
            titulo = "Sustrato y oxigenación",
            descripcion = "Mezcla para cactus o plantas grasas con perlita " +
                    "o arena volcánica. La maceta debe contar obligatoriamente " +
                    "con orificio de evacuación de agua."
        ),
        PasoCuidado(
            titulo = "Ritual de brillo",
            descripcion = "Limpia el polvo de los foliolos con un paño de " +
                    "microfibra humedecido con agua templada una vez al mes " +
                    "para optimizar su fotosíntesis."
        )
    ),

    val enListaDeseos: Boolean = false
)