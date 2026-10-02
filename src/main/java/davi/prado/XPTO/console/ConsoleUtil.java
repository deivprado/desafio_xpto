package davi.prado.XPTO.console;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

/**
 * Leitura de dados do teclado e formatação de data/dinheiro para o console.
 */
public final class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final NumberFormat MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private ConsoleUtil() {
    }

    /** Lê um texto. */
    public static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return SCANNER.nextLine().trim();
    }

    /** Igual ao lerTexto, mas devolve null quando o usuário só aperta ENTER. */
    public static String lerTextoOpcional(String mensagem) {
        String texto = lerTexto(mensagem);
        return texto.isEmpty() ? null : texto;
    }

    /** Lê um número inteiro, repetindo enquanto o usuário digitar algo inválido. */
    public static int lerInt(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem));
            } catch (NumberFormatException e) {
                System.out.println("  Digite um número válido.");
            }
        }
    }

    /** Lê um valor em reais. Pode digitar com vírgula (ex: 1500,00). */
    public static BigDecimal lerValor(String mensagem) {
        while (true) {
            try {
                return new BigDecimal(lerTexto(mensagem).replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("  Digite um valor válido. Ex.: 1500,00");
            }
        }
    }

    /** Lê uma data no formato dd/MM/aaaa. */
    public static LocalDate lerData(String mensagem) {
        while (true) {
            try {
                return LocalDate.parse(lerTexto(mensagem), DATA);
            } catch (Exception e) {
                System.out.println("  Digite a data no formato dd/MM/aaaa. Ex.: 02/10/2026");
            }
        }
    }

    /** Igual ao lerData, mas devolve null quando o usuário só aperta ENTER. */
    public static LocalDate lerDataOpcional(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (texto.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(texto, DATA);
            } catch (Exception e) {
                System.out.println("  Digite a data no formato dd/MM/aaaa ou ENTER.");
            }
        }
    }

    public static void pausar() {
        System.out.print("\nPressione ENTER para continuar...");
        SCANNER.nextLine();
    }

    public static String data(LocalDate valor) {
        return valor == null ? "-" : DATA.format(valor);
    }

    public static String data(LocalDateTime valor) {
        return valor == null ? "-" : DATA.format(valor);
    }

    public static String dataHora(LocalDateTime valor) {
        return valor == null ? "-" : DATA_HORA.format(valor);
    }

    public static String dinheiro(BigDecimal valor) {
        return valor == null ? "-" : MOEDA.format(valor);
    }
}
