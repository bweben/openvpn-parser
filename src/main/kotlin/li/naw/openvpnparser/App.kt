package li.naw.openvpnparser

import li.naw.openvpnparser.parsing.Parser

fun main(args: Array<String>) {
    for (arg in args) {
        if (arg.toLowerCase() == "-str") {
            println(Parser().parse(args.sliceArray(IntRange(args.indexOf(arg) + 1, args.size - 1)).joinToString(" ").replace("\\n ", "\n")))
            return
        }
    }

    println("Please provide a string with the argument '-str \"string to parse\"'")
    return
}