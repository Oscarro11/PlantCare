# PlantCare

Esta es una aplicación enfocada a usuarios novicios, los cuales están empezando en el
hobby de jardinería. Se les ayuda con la identificacion de plantas y su estado de salud, junto
con seguimiento de tareas diarias y recomendaciones para expandir su jardín.

## Servicios

Esta aplicación usa los siguientes servicios:
- Pl@ntNet: este servicio proporciona información de diferentes plantas, siendo capaz de realizar
            diagnósticos y recomendaciones en base a imágenes. Funciona a base de cuotas gratuitas,
            teniendo alrededor de 2000 créditos diarios.

- Firebase: este servicio proporciona autenticación de usuarios, además de ser una base de datos 
          para archivos complejos como lo son imágenes. Este servicio tiene un límite gratuito, 
          necesitando una cuenta de desarrollador con una forma de pago asociada a ella.

## Librerías

Esta aplicación usa las siguientes librerías:
- Coil: sirve para mostrar imágenes de forma local, siempre que se le provea una URL adecuada. El 
        servicio de Firebase se encarga de generar la URL de las imágenes subidas por usuarios.
- Hilt: sirve para poder crear viewModels a partir de diferentes repositorios, dando un constructor
        default con datos falsos para previsualizaciones