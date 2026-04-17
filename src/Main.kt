fun main() {
    println(" ** --BUSCAMINAS-- **")
    println("D = destapar | B = bandera")

    //Pedimos los datos para crear la partida, tenía que ser personalizable

    val filas = leerEntero("Filas: ")
    val columnas = leerEntero("Columnas: ")
    val minas = leerEntero("Minas: ")


    //Creamos el juego con la configuración elegida
    val juego = Buscaminas(filas, columnas, minas)

    while (!juego.juegoTerminado) {
        mostrarTablero(juego)


        //Pedimos al usuario que quiere hacer
        print("Accion (D/B): ")
        val accion = readLine()!!.uppercase()

        //Posición donde va a actuar
        val fila = leerEntero("Fila: ")
        val columna = leerEntero("Columna: ")


        //Si pulda D, llamamos a destapar() y si pulsa B llamamos a gestionarBandera()
        if (accion == "D") {
            juego.destapar(fila, columna)
        } else if (accion == "B") {
            juego.gestionarBandera(fila, columna)
        }
    }
    //Cuando termina la partida, muestra el tablero final
    mostrarTablero(juego)


    //Mostramos si se gana o pierde
    if (juego.victoria) {
        println("Has ganado")
    } else {
        println("Has perdido")
    }
}

fun leerEntero(texto: String): Int {
    print(texto)
    return readLine()!!.toInt()
}

fun mostrarTablero(juego: Buscaminas) {
    val tablero = juego.obtenerTablero()

    print("   ")
    for (c in 0 until juego.columnas) {
        print("$c ")
    }
    println()


    //Aqui se recorre fila por fila para dibujar el tablero
    for (f in 0 until juego.filas) {
        print("$f  ")
        for (c in 0 until juego.columnas) {
            val celda = tablero[f][c]
            //Elegimos que símbolo se enseña según el estado de la celda
            val simbolo = when {        //iconos en unicode
                celda.tieneBandera -> "\uD83D\uDEA9"
                celda.estaTapada -> "*"
                celda.esMina -> "\uD83D\uDCA3"
                celda.minasVecinas == 0 -> " "
                else -> celda.minasVecinas.toString()
            }

            print("$simbolo ")
        }
        println()
    }
}