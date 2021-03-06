package compiladorcefetiny;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Aline, Eduardo Cotta, Luiz, Pedro Lucas e Ruan
 */
public class LeArquivo implements CanalEntrada {

    private String textoArquivo;
    private int contadorCaractere;

    public LeArquivo() {
        textoArquivo  = "b:= not(true)+5";
    }

    public void fileToString() {

        Scanner le = new Scanner(System.in);

        System.out.println("Entre com o o endereco do arquivo.");
        String caminho = le.nextLine();

        try {
            FileReader arq = new FileReader(caminho);
            BufferedReader leArq = new BufferedReader(arq);

            String linha;
            do {
                linha = leArq.readLine();
                if (linha != null) {
                    textoArquivo += linha + "\n";
                }
            } while (linha != null);

        } catch (IOException ex) {
            ex.getMessage();
        }
    }

    @Override
    public void zeraContador(){
        contadorCaractere=0;
    }

    @Override
    public char get() {
        if (contadorCaractere == textoArquivo.length()) {
            return 0;
        }        
        char caractere = textoArquivo.charAt(contadorCaractere);
        contadorCaractere++;
        return caractere;
    }

    @Override
    public void unget() {
        contadorCaractere--;
    }
}
