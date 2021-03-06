package compiladorcefetiny;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aline, Eduardo Cotta, Luiz, Pedro Lucas e Ruan
 */
public class AnaliseSintaticaComandos {

    private String tipoComandoLido;
    private String expressaoPreparada;
    private CanalEntrada input;
    private int contaLinhas = 0;
    private MontaComandos montaComandos = new MontaComandos();

    public AnaliseSintaticaComandos(CanalEntrada input) {
        this.expressaoPreparada = "";
        this.tipoComandoLido = "";
        this.input = input;

    }


    public void terceiraAnaliseSintatica() throws ExcecaoSintaxeIncorreta {
        identificaComandos(true, false);
    }

    public PseudoComando identificaComandos(boolean primeiroIdentifica, boolean isInsideCommand) throws ExcecaoSintaxeIncorreta {

        PseudoComando tempComando = null;
        String palavraLida = "";
        char caractere = input.get();
        boolean esperaComandoAtribuicao = false;
        boolean palavraPreenchida = false;
        boolean fimComandos = false;
        while (caractere != 0 && !fimComandos) {
            String tipoCaractere = identificaTipoCaractere(caractere);
            if ((tipoCaractere.equals("parenteses-abre") || tipoCaractere.equals("espaco")) && !esperaComandoAtribuicao && palavraPreenchida) {
                input.unget();
                int retornoTipo = isComando(identificaPalavrasReservadas(palavraLida));
                switch (retornoTipo) {
                    case 0:
                        if (palavraLida.equals("endif")) {
                            tempComando = new PseudoComando(null, "endif", "endif", false);
                            return tempComando;
                        } else if (palavraLida.equals("endwhile")) {
                            tempComando = new PseudoComando(null, "endwhile", "endwhile", false);
                            return tempComando;
                        } else if (palavraLida.equals("endfor")) {
                            tempComando = new PseudoComando(null, "endfor", "endfor", false);
                            return tempComando;
                        } else {
                            throw new ExcecaoSintaxeIncorreta("Palavra inserida em uma expressão incorreta" + "    Linha: " + contaLinhas);
                        }
                    case 1:
                        esperaComandoAtribuicao = true;
                        break;
                    case 2:
                        if (palavraLida.equals("if")) {

                            tempComando = analiseComandoIf(palavraLida, isInsideCommand);
                            if (primeiroIdentifica) {
                                montaComandos.montaLista(tempComando, null, false);
                            } else {
                                return tempComando;
                            }
                            palavraLida = "";
                            palavraPreenchida = false;
                        } else if (palavraLida.equals("for")) {
                            tempComando = analiseComandoFor(palavraLida, isInsideCommand);
                            if (primeiroIdentifica) {
                                montaComandos.montaLista(tempComando, null, false);
                            } else {
                                return tempComando;
                            }
                            palavraLida = "";
                            palavraPreenchida = false;
                        } else if (palavraLida.equals("while")) {
                            tempComando = analiseComandoWhile(palavraLida, isInsideCommand);
                            if (primeiroIdentifica) {
                                montaComandos.montaLista(tempComando, null, false);

                            } else {
                                return tempComando;
                            }
                            palavraLida = "";
                            palavraPreenchida = false;
                        } else if (palavraLida.equals("print")) {
                            tempComando = analiseComandoPrint(palavraLida);
                            if (primeiroIdentifica) {

                                System.out.println(tempComando.getStringComando());
                                montaComandos.montaLista(tempComando, null, false);
                            } else {
                                return tempComando;
                            }
                            palavraLida = "";
                        } else if (palavraLida.equals("println")) {
                            tempComando = analiseComandoPrintln(palavraLida);
                            if (primeiroIdentifica) {

                                montaComandos.montaLista(tempComando, null, false);

                            } else {
                                return tempComando;
                            }
                            palavraLida = "";
                            palavraPreenchida = false;
                        } else if (palavraLida.equals("readint")) {
                            tempComando = analiseComandoReadint(palavraLida);
                            if (primeiroIdentifica) {

                                montaComandos.montaLista(tempComando, null, false);
                            } else {
                                return tempComando;
                            }
                            palavraLida = "";
                            palavraPreenchida = false;
                        } else if (palavraLida.equals("else")) {
                            tempComando = analiseComandoIf(palavraLida, isInsideCommand);
                            
                            palavraLida = "";
                            palavraPreenchida = false;
                            
                            return tempComando;
                        } else if (palavraLida.equals("end")) {
                            if (primeiroIdentifica) {

                                fimComandos = true;
                            } else {
                                throw new ExcecaoSintaxeIncorreta("Fim de programa em lugar errado!" + "    Linha: " + contaLinhas);
                            }
                        }
                        break;
                }

            } else if (tipoCaractere.equals("dois-pontos") || (tipoCaractere.equals("dois-pontos") && esperaComandoAtribuicao)) {
                esperaComandoAtribuicao = false;
                char caractereTemp = input.get();
                if (caractereTemp == '=') {
                    palavraLida += caractere;
                    palavraLida += caractereTemp;
                    tempComando = analiseComandoAtribuicao(palavraLida);
                    if (primeiroIdentifica) {

                        System.out.println(tempComando.getStringComando());
                        montaComandos.montaLista(tempComando, null, false);
                    } else {
                        return tempComando;
                    }
                    palavraLida = "";
                    palavraPreenchida = false;
                } else {
                    throw new ExcecaoSintaxeIncorreta("Caractere não esperado!" + "    Linha: " + contaLinhas);
                }
            } else if ((tipoCaractere.equals("letra") || tipoCaractere.equals("numero"))) {
                palavraPreenchida = true;
                palavraLida += caractere;
            } else if (!tipoCaractere.equals("espaco") && !tipoCaractere.equals("quebra-linha")) {
                throw new ExcecaoSintaxeIncorreta("Caractere inválido no contexto!" + "    Linha: " + contaLinhas);
            }
            //System.out.println( palavraLida);
            caractere = input.get();
        }

        if (palavraPreenchida && primeiroIdentifica) {
            int retornoTipo = isComando(identificaPalavrasReservadas(palavraLida));
            if (retornoTipo == 2) {
                if (palavraLida.equals("end")) {
                    //Comando final
                } else {
                    throw new ExcecaoSintaxeIncorreta("Programa não tem fim!" + "    Linha: " + contaLinhas);
                }
            }
        }
        return tempComando;
    }

    private String identificaTipoCaractere(char caractere) {
        String tipo = "";

        if (caractere > 96 && caractere < 123) {
            tipo = "letra";
        } else if (caractere > 47 && caractere < 58) {
            tipo = "numero";
        } else if (caractere == 32) {
            tipo = "espaco";
        } else if (caractere == 42 || caractere == 43 || caractere == 45 || caractere == 47) {
            tipo = "op-aritmetico";
        } else if (caractere == 60 || caractere == 62) {
            tipo = "op-logico";
        } else if (caractere == 58) {
            tipo = "dois-pontos";
        } else if (caractere == 61) {
            tipo = "igual";
        } else if (caractere == 46) {
            tipo = "ponto";
        } else if (caractere == 34) {
            tipo = "aspas";
        } else if (caractere == 40) {
            tipo = "parenteses-abre";
        } else if (caractere == 41) {
            tipo = "parenteses-fecha";
        } else if (caractere == 10) {
            tipo = "quebra-linha";
        }
        return tipo;
    }

    private String identificaPalavrasReservadas(String palavraObservada) throws ExcecaoSintaxeIncorreta {
        String tipoPalavra = "";
        String vetorPalavras[] = {"if", "then", "endif", "else", "while", "do", "endwhile", "for", "to", "downto", "endfor", "print", "println", "readint", "sqrt", "not", "true", "false", "or", "and", "mod", "div", "end"};

        for (String palavraChecada : vetorPalavras) {
            if (palavraObservada.equals(palavraChecada)) {
                return palavraChecada;
            }
        }
        int verifica = validaVariavel(palavraObservada);
        if (verifica == 1) {
            throw new ExcecaoSintaxeIncorreta("Caracteres inválidos!" + "    Linha: " + contaLinhas);
        } else if (verifica == 2) {
            throw new ExcecaoSintaxeIncorreta("Caracteres inválidos!" + "    Linha: " + contaLinhas);
        } else if (verifica == 0) {
            tipoPalavra = "variavel";
        } else {
            return "numero";
        }
        return tipoPalavra;
    }

    private int validaVariavel(String palavraObservada) {
        char arrayTemp[] = palavraObservada.toCharArray();
        boolean existeNumeroPrimeiro = false;

        if (!isNumero(palavraObservada)) {
            existeNumeroPrimeiro = isNumero(String.valueOf(arrayTemp[0]));

            if (existeNumeroPrimeiro) {
                for (char caractere : arrayTemp) {
                    if (identificaTipoCaractere(caractere).equals("letra")) {
                        return 1;
                    }
                }
            }
            for (char caractere : arrayTemp) {
                if (!identificaTipoCaractere(caractere).equals("letra") && !identificaTipoCaractere(caractere).equals("numero")) {
                    return 2;
                }
            }
        } else {
            return 4;
        }
        return 0;
    }

    private int isComando(String tipo) {
        switch (tipo) {
            case "numero":
                return 4;
            case "end":
            case "if":
            case "for":
            case "while":
            case "print":
            case "println":
            case "readint":
                return 2;
            case "variavel":
                return 1;
            default:
                return 0;
        }
    }

    private String analiseExpressao(String palavraLida, int indexParada, boolean expressaoLimitada) throws ExcecaoSintaxeIncorreta {
        String comandoLido = "";
        String palavraParcial = "";
        String tipoCaractere = "";
        String bufferVariavel = "";
        boolean isComando = true;
        char caractere = input.get();

        boolean variavelAnterior = false;
        boolean operadorBinarioAnterior = false;
        boolean operadorUnarioAnterior = false;
        boolean numeroAnterior = false;
        boolean operadorAnterior = false;
        boolean expressaoAnterior = false;
        boolean constanteBoolAnterior = false;
        boolean existeAspas = false;

        int contaParentesesAbre = 0;
        int contaParentesesFecha = 0;
        int contaAspas = 0;
        int contaIteraçoes = 0;
        int contaEspacos = 0;

        tipoCaractere = identificaTipoCaractere(caractere);
        while (((caractere != 0) && isComando)) {
            if (tipoCaractere.equals("parenteses-abre")) {
                contaEspacos = 0;
                if (!(palavraParcial.equals(""))) {
                    String tempTipoPalavra = identificaPalavrasReservadas(palavraParcial);
                    int retornoComando = isComando(tempTipoPalavra);

                    if (retornoComando == 0 && !variavelAnterior) {
                        if (tempTipoPalavra.equals("not") || tempTipoPalavra.equals("sqrt")) {
                            String verificada = palavraParcial + '(' + verificaOperadorUnario();
                            operadorUnarioAnterior = true;
                            operadorBinarioAnterior = false;
                            numeroAnterior = false;
                            operadorAnterior = false;
                            palavraParcial = "";
                            comandoLido += verificada;
                        } else if (!(comandoLido.equals("")) && !(operadorAnterior) && !(operadorBinarioAnterior)) {
                            isComando = false;
                            break;
                        }
                    } else if (retornoComando == 2) {
                        if (!expressaoLimitada) {
                            if (palavraParcial.equals("if") || palavraParcial.equals("print") || palavraParcial.equals("println") || palavraParcial.equals("while") || palavraParcial.equals("readint")) {
                                isComando = false;
                                break;
                            }
                        }
                    } else if (retornoComando != 4) {
                        throw new ExcecaoSintaxeIncorreta("Tipo de palavra errado" + "    Linha: " + contaLinhas);
                    }

                } else {
                    palavraParcial += caractere;
                    contaParentesesAbre++;
                    expressaoAnterior = true;
                }
            } else if (tipoCaractere.equals("parenteses-fecha")) {
                contaEspacos = 0;
                palavraParcial += caractere;
                contaParentesesFecha++;
                numeroAnterior = false;
            } else if (tipoCaractere.equals("aspas")) {
                contaEspacos = 0;
                if (tipoCaractere.equals("aspas")) {
                    existeAspas = !existeAspas;
                }
                palavraParcial += caractere;
                contaAspas++;
            } else if (tipoCaractere.equals("op-aritmetico") || (tipoCaractere.equals("op-logico") && !operadorAnterior) || (tipoCaractere.equals("igual") && !operadorAnterior)) {
                contaEspacos = 0;
                if (!expressaoAnterior && !variavelAnterior) {
                    String tempTipoPalavra = identificaPalavrasReservadas(palavraParcial);
                    int retornoComando = isComando(tempTipoPalavra);

                    if (retornoComando == 0) {
                        if (tempTipoPalavra.equals("true") || tempTipoPalavra.equals("false")) {
                            if (tipoCaractere.equals("op-aritmetico")) {
                                throw new ExcecaoSintaxeIncorreta("Operador aritmético inválido!" + "    Linha: " + contaLinhas);
                            } else {
                                constanteBoolAnterior = true;
                            }
                        } else {
                            throw new ExcecaoSintaxeIncorreta("Operador binário fora de contexto!" + "    Linha: " + contaLinhas);
                        }
                    } else if (retornoComando == 1) {
                        if (variavelAnterior || numeroAnterior || expressaoAnterior || constanteBoolAnterior || operadorUnarioAnterior) {
                            throw new ExcecaoSintaxeIncorreta("Variável fora de contexto!" + "    Linha: " + contaLinhas);
                        } else {
                            variavelAnterior = true;
                        }
                    } else if (retornoComando != 4) {
                        throw new ExcecaoSintaxeIncorreta("Tipo de palavra errado!" + "    Linha: " + contaLinhas);
                    }
                }
                if (variavelAnterior || numeroAnterior || expressaoAnterior || constanteBoolAnterior || operadorUnarioAnterior) {
                    variavelAnterior = false;
                    numeroAnterior = false;
                    expressaoAnterior = false;
                    constanteBoolAnterior = false;
                    operadorUnarioAnterior = false;
                    operadorAnterior = true;

                    palavraParcial += caractere;
                    if (!bufferVariavel.equals("")) {
                        comandoLido += bufferVariavel;
                        bufferVariavel = "";
                    }
                    comandoLido += palavraParcial;

                    palavraParcial = "";

                } else {
                    throw new ExcecaoSintaxeIncorreta("Erro! Sintaxe Incorreta!" + "    Linha: " + contaLinhas);
                }
                if (contaParentesesAbre - contaParentesesFecha == 0) {
                    expressaoAnterior = false;
                }
            } else if ((tipoCaractere.equals("espaco") || tipoCaractere.equals("quebra-linha")) && !palavraParcial.equals("") && !existeAspas) {
                String tempTipoPalavra = identificaPalavrasReservadas(palavraParcial);
                int retornoComando = isComando(tempTipoPalavra);

                if (tipoCaractere.equals("quebra-linha")) {
                    contaLinhas++;
                }
                if (numeroAnterior && retornoComando == 4) {
                    comandoLido += palavraParcial;
                    palavraParcial = "";
                } else if (retornoComando == 1) {
                    boolean existeAtribuicaoDepois = verificaAtribuicaoDepois();

                    if ((!variavelAnterior && !expressaoAnterior && !constanteBoolAnterior && !operadorUnarioAnterior && !numeroAnterior) || existeAtribuicaoDepois) {
                        variavelAnterior = true;
                        operadorAnterior = false;
                        operadorBinarioAnterior = false;
                        expressaoAnterior = false;
                        constanteBoolAnterior = false;
                        operadorUnarioAnterior = false;
                        numeroAnterior = false;
                        bufferVariavel = palavraParcial;
                        palavraParcial = "";
                    } else {
                        throw new ExcecaoSintaxeIncorreta("Variável usada em contexto errado!" + "    Linha: " + contaLinhas);
                    }
                } else if (retornoComando == 0) {
                    if (palavraParcial.equals("and") || palavraParcial.equals("or") || palavraParcial.equals("mod") || palavraParcial.equals("div")) {
                        if ((variavelAnterior || operadorUnarioAnterior || constanteBoolAnterior || numeroAnterior || expressaoAnterior) && !operadorBinarioAnterior) {
                            operadorBinarioAnterior = true;
                            expressaoAnterior = false;
                            constanteBoolAnterior = false;
                            operadorUnarioAnterior = false;
                            numeroAnterior = false;
                            variavelAnterior = false;
                            operadorAnterior = false;

                            String tempStr = " " + palavraParcial + " ";

                            if (!bufferVariavel.equals("")) {
                                comandoLido += bufferVariavel;
                                bufferVariavel = "";
                            }
                            comandoLido += tempStr;
                            palavraParcial = "";
                        } else {
                            throw new ExcecaoSintaxeIncorreta("Palavra Anterior Incorreta!" + "    Linha: " + contaLinhas);
                        }
                    } else if (palavraParcial.equals("true") || palavraParcial.equals("false")) {
                        if (!variavelAnterior || !expressaoAnterior || !constanteBoolAnterior || !operadorUnarioAnterior || !numeroAnterior) {
                            operadorBinarioAnterior = false;
                            expressaoAnterior = false;
                            constanteBoolAnterior = true;
                            operadorUnarioAnterior = false;
                            numeroAnterior = false;
                            variavelAnterior = false;
                            operadorAnterior = false;

                            if (!bufferVariavel.equals("")) {
                                comandoLido += bufferVariavel;
                                bufferVariavel = "";
                            }
                            comandoLido += palavraParcial;
                            palavraParcial = "";
                        } else {
                            throw new ExcecaoSintaxeIncorreta("Palavra Anterior Incorreta!" + "    Linha: " + contaLinhas);
                        }
                    }
                } else if (retornoComando != 4) {
                    if (retornoComando == 2 && !expressaoLimitada) {
                        isComando = false;
                        break;
                    }
                    throw new ExcecaoSintaxeIncorreta("Palavra Anterior Incorreta!" + "    Linha: " + contaLinhas);
                }
            } else if (tipoCaractere.equals("numero")) {
                contaEspacos = 0;
                if (comandoLido.equals("") || operadorBinarioAnterior || operadorAnterior || numeroAnterior) {
                    if (palavraParcial.equals("")) {
                        numeroAnterior = true;
                    }
                    palavraParcial += caractere;
                } else {
                    throw new ExcecaoSintaxeIncorreta("Palavra Anterior Incorreta!" + "    Linha: " + contaLinhas);
                }
            } else if (tipoCaractere.equals("letra")) {
                contaEspacos = 0;
                palavraParcial += caractere;
            } else if (tipoCaractere.equals("ponto")) {
                contaEspacos = 0;
                if (numeroAnterior) {
                    palavraParcial += caractere;
                } else {
                    throw new ExcecaoSintaxeIncorreta("Ponto fora do lugar esperado!" + "    Linha: " + contaLinhas);
                }
            } else if (tipoCaractere.equals("igual")) {
                contaEspacos = 0;
                if (operadorAnterior || variavelAnterior || numeroAnterior || constanteBoolAnterior || expressaoAnterior || operadorUnarioAnterior) {
                    palavraParcial += caractere;
                }
            } else if (tipoCaractere.equals("dois-pontos")) {
                char proxCaractere = input.get();
                int posicoesAnteriores = 0;
                if (proxCaractere == '=') {
                    if (bufferVariavel.equals("") && !palavraParcial.equals("")) {
                        String tempTipoPalavra = identificaPalavrasReservadas(palavraParcial);
                        int retornoComando = isComando(tempTipoPalavra);
                        if (retornoComando == 1) {
                            posicoesAnteriores = palavraParcial.length() + 2;
                        } else {
                            throw new ExcecaoSintaxeIncorreta("Palavra incorreta dentro do contexto!" + "    Linha: " + contaLinhas);
                        }
                    } else {
                        comandoLido += bufferVariavel;
                        posicoesAnteriores = bufferVariavel.length() + 1 + contaEspacos + 2;
                    }
                    for (int i = 0; i < posicoesAnteriores; i++) {
                        input.unget();
                    }
                    isComando = false;
                    break;
                } else {
                    throw new ExcecaoSintaxeIncorreta("Palavra Anterior Incorreta!" + "    Linha: " + contaLinhas);
                }
            } else if (tipoCaractere.equals("espaco") || tipoCaractere.equals("quebra-linha")) {
                contaEspacos++;
            } else {
                throw new ExcecaoSintaxeIncorreta("Caractere inválido no contexto!" + "    Linha: " + contaLinhas);
            }
            caractere = input.get();
            tipoCaractere = identificaTipoCaractere(caractere);
            contaIteraçoes++;
            if ((contaIteraçoes == indexParada) && expressaoLimitada) {
                break;
            }
        }

        //Fim while
        if (contaParentesesAbre - contaParentesesFecha != 0) {
            throw new ExcecaoSintaxeIncorreta("Parenteses usados de forma incorreta!" + "    Linha: " + contaLinhas);
        }
        if (contaAspas % 2 != 0) {
            //Exception, aspas errados
            throw new ExcecaoSintaxeIncorreta("Aspas usadas de forma incorreta!" + "    Linha: " + contaLinhas);
        }
        int tamanhoPalavra = 0;

        if (operadorAnterior && palavraParcial.equals("") && bufferVariavel.equals("")) {
            throw new ExcecaoSintaxeIncorreta("Operador usado em contexto errado!" + "    Linha: " + contaLinhas);
        } else {
            tamanhoPalavra = palavraParcial.length();
        }

        if (tamanhoPalavra == 1 && isComando) {
            char ultimoChar = palavraParcial.charAt(0);
            String tipoUltimoChar = identificaTipoCaractere(ultimoChar);

            if (tipoUltimoChar.equals("letra")) {
                if (variavelAnterior || operadorUnarioAnterior || numeroAnterior || expressaoAnterior || constanteBoolAnterior) {
                    throw new ExcecaoSintaxeIncorreta("Palavra usada em contexto errado!" + "    Linha: " + contaLinhas);
                }
            }
        } else if (tamanhoPalavra > 1 && isComando) {
            String tempTipoPalavra = identificaPalavrasReservadas(palavraParcial);
            int retornoComando = isComando(tempTipoPalavra);
            if (retornoComando != 1) {
                if (retornoComando == 0) {
                    if (!palavraParcial.equals("true") && !palavraParcial.equals("false")) {
                        throw new ExcecaoSintaxeIncorreta("Palavra usada em contexto errado!" + "    Linha: " + contaLinhas);
                    }
                } else if (retornoComando == 2) {
                    throw new ExcecaoSintaxeIncorreta("Comando usado em contexto errado!" + "    Linha: " + contaLinhas);
                }
            } else if (variavelAnterior || expressaoAnterior || constanteBoolAnterior || operadorUnarioAnterior || numeroAnterior) {
                //Exception, variável inserida fora de contexto
                throw new ExcecaoSintaxeIncorreta("Variável usada em contexto errado!" + "    Linha: " + contaLinhas);
            }
        }
        if (isComando) {
            comandoLido += palavraParcial;
        }

        return palavraLida + comandoLido;
    }

    private boolean isNumero(String possivelNumero) {
        char arrayTemp[] = possivelNumero.toCharArray();

        for (char charTemp : arrayTemp) {
            if (!((charTemp > 47 && charTemp < 58) || charTemp == 46)) {
                return false;
            }
        }
        return true;
    }

    private PseudoComando analiseComandoAtribuicao(String palavraLida) throws ExcecaoSintaxeIncorreta {
        String retornoExpressao = analiseExpressao(palavraLida, 0, false);

        return null;
    }

    private PseudoComando analiseComandoIf(String palavraLida, Boolean isInsideCommand) throws ExcecaoSintaxeIncorreta {

        if (!isInsideCommand) {
            int contAbreParenteses = 0;
            int contFechaParenteses = 0;
            int contNumeroGet = 0;
            int tempNumGet = 0;
            char caractere = input.get();
            while (caractere != 40) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            while (!(contFechaParenteses > contAbreParenteses)) {
                contNumeroGet++;
                caractere = input.get();
                if (caractere == 40) {
                    contAbreParenteses++;
                }
                if (caractere == 41) {
                    contFechaParenteses++;
                }
            }
            tempNumGet = contNumeroGet;
            while (contNumeroGet != 0) {
                input.unget();
                contNumeroGet--;
            }
            String tempPalavraLida = palavraLida + "(";
            String expressao = analiseExpressao(tempPalavraLida, (tempNumGet - 1), true);
            expressao = expressao + input.get();
            caractere = input.get();
            while (caractere != 116) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            String palavraReservada = String.valueOf(caractere);
            for (int i = 0; i < 3; i++) {
                palavraReservada += input.get();
            }
            caractere = input.get();
            if (!(palavraReservada.equals("then")) && caractere != 32) {
                //exception
            }

            List<PseudoComando> pseudoListaIf = new ArrayList<>();
            PseudoComando tempComando = identificaComandos(false, false);

            while ((!tempComando.getTipoComando().equals("endif")) && (!tempComando.getTipoComando().equals("else"))) {
                pseudoListaIf.add(tempComando);
                tempComando = identificaComandos(false, false);
            }

            if (tempComando.getTipoComando().equals("else")) {
                List<PseudoComando> pseudoListaElse = new ArrayList<>();
                tempComando = identificaComandos(false, false);
                while ((!tempComando.getTipoComando().equals("endif"))) {
                    pseudoListaElse.add(tempComando);
                    tempComando = identificaComandos(false, false);
                }
                PseudoComando comandoElse = new PseudoComando(pseudoListaElse, "else", expressao, true);
                pseudoListaIf.add(comandoElse);
            }

            return new PseudoComando(pseudoListaIf, "if", expressao, true);
        } else {
            int contAbreParenteses = 0;
            int contFechaParenteses = 0;
            int contNumeroGet = 0;
            int tempNumGet = 0;
            char caractere = input.get();
            while (caractere != 40) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            while (!(contFechaParenteses > contAbreParenteses)) {
                contNumeroGet++;
                caractere = input.get();
                if (caractere == 40) {
                    contAbreParenteses++;
                }
                if (caractere == 41) {
                    contFechaParenteses++;
                }
            }
            tempNumGet = contNumeroGet;
            while (contNumeroGet != 0) {
                input.unget();
                contNumeroGet--;
            }
            String tempPalavraLida = palavraLida + "(";
            String expressao = analiseExpressao(tempPalavraLida, (tempNumGet - 1), true);
            expressao = expressao + input.get();
            caractere = input.get();
            while (caractere != 116) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            String palavraReservada = String.valueOf(caractere);
            for (int i = 0; i < 3; i++) {
                palavraReservada += input.get();
            }
            caractere = input.get();
            if (!(palavraReservada.equals("then")) && caractere != 32) {
                //exception
            }

            List<PseudoComando> pseudoListaIfInterna = new ArrayList<>();
            PseudoComando tempComandoInterno = identificaComandos(false, true);

            while ((!tempComandoInterno.getTipoComando().equals("endif")) && (!tempComandoInterno.getTipoComando().equals("else"))) {
                pseudoListaIfInterna.add(tempComandoInterno);
                tempComandoInterno = identificaComandos(false, true);
            }

            if (tempComandoInterno.getTipoComando().equals("else")) {
                List<PseudoComando> pseudoListaElseInterna = new ArrayList<>();
                tempComandoInterno = identificaComandos(false, true);
                while ((!tempComandoInterno.getTipoComando().equals("endif"))) {
                    pseudoListaElseInterna.add(tempComandoInterno);
                    tempComandoInterno = identificaComandos(false, true);
                }
                PseudoComando comandoElse = new PseudoComando(pseudoListaElseInterna, "else", expressao, true);
                pseudoListaIfInterna.add(comandoElse);
            }

            return new PseudoComando(pseudoListaIfInterna, "if", expressao, true);
        }
    }

    private PseudoComando analiseComandoFor(String palavraLida, Boolean isInsideCommand) {
        if(!isInsideCommand){
            
        }else{
            
        }
        return null;
    }

    private PseudoComando analiseComandoWhile(String palavraLida, Boolean isInsideCommand) throws ExcecaoSintaxeIncorreta {
        if (!isInsideCommand) {
            int contAbreParenteses = 0;
            int contFechaParenteses = 0;
            int contNumeroGet = 0;
            int tempNumGet = 0;
            char caractere = input.get();
            while (caractere != 40) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            while (!(contFechaParenteses > contAbreParenteses)) {
                contNumeroGet++;
                caractere = input.get();
                if (caractere == 40) {
                    contAbreParenteses++;
                }
                if (caractere == 41) {
                    contFechaParenteses++;
                }
            }
            tempNumGet = contNumeroGet;
            while (contNumeroGet != 0) {
                input.unget();
                contNumeroGet--;
            }
            String tempPalavraLida = palavraLida + "(";
            String expressao = analiseExpressao(tempPalavraLida, (tempNumGet - 1), true);
            expressao = expressao + input.get();

            caractere = input.get();
            while (caractere != 100) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            caractere = input.get();
            if (caractere == 111) {
                caractere = input.get();
                if (caractere != 32) {
                    //exception
                }
            } else {
                //exception
            }
            List<PseudoComando> pseudoListaWhile = new ArrayList<>();
            PseudoComando tempComando = identificaComandos(false, false);
            while ((!tempComando.getTipoComando().equals("endwhile"))) {
                pseudoListaWhile.add(tempComando);
                tempComando = identificaComandos(false, false);
            }
            return new PseudoComando(pseudoListaWhile, "while", expressao, true);
        }else{
            int contAbreParenteses = 0;
            int contFechaParenteses = 0;
            int contNumeroGet = 0;
            int tempNumGet = 0;
            char caractere = input.get();
            while (caractere != 40) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            while (!(contFechaParenteses > contAbreParenteses)) {
                contNumeroGet++;
                caractere = input.get();
                if (caractere == 40) {
                    contAbreParenteses++;
                }
                if (caractere == 41) {
                    contFechaParenteses++;
                }
            }
            tempNumGet = contNumeroGet;
            while (contNumeroGet != 0) {
                input.unget();
                contNumeroGet--;
            }
            String tempPalavraLida = palavraLida + "(";
            String expressao = analiseExpressao(tempPalavraLida, (tempNumGet - 1), true);
            expressao = expressao + input.get();

            caractere = input.get();
            while (caractere != 100) {
                if (caractere == 32) {
                    caractere = input.get();
                } else {
                    //exception
                }
            }
            caractere = input.get();
            if (caractere == 111) {
                caractere = input.get();
                if (caractere != 32) {
                    //exception
                }
            } else {
                //exception
            }
            List<PseudoComando> pseudoListaWhileInterna = new ArrayList<>();
            PseudoComando tempComandoInterno = identificaComandos(false, true);
            while ((!tempComandoInterno.getTipoComando().equals("endwhile"))) {
                pseudoListaWhileInterna.add(tempComandoInterno);
                tempComandoInterno = identificaComandos(false, true);
            }
            return new PseudoComando(pseudoListaWhileInterna, "while", expressao, true);
        }
    }

    private PseudoComando analiseComandoPrint(String palavraLida) throws ExcecaoSintaxeIncorreta {
        int contAbreParenteses = 0;
        int contFechaParenteses = 0;
        int contNumeroGet = 0;
        int tempNumGet = 0;
        char caractere = input.get();
        while (caractere != 40) {
            if (caractere == 32) {
                caractere = input.get();
            } else {
                //exception	
            }
        }
        while (!(contFechaParenteses > contAbreParenteses)) {
            contNumeroGet++;
            caractere = input.get();
            if (caractere == 40) {
                contAbreParenteses++;
            }
            if (caractere == 41) {
                contFechaParenteses++;
            }
        }
        tempNumGet = contNumeroGet;
        while (contNumeroGet != 0) {
            input.unget();
            contNumeroGet--;
        }
        String tempPalavraLida = palavraLida + "(";
        String expressao = analiseExpressao(tempPalavraLida, (tempNumGet - 1), true);
        expressao = expressao + input.get();
        PseudoComando tempComando = new PseudoComando(null, expressao, "print", false);

        return tempComando;
    }

    private PseudoComando analiseComandoPrintln(String palavraLida) throws ExcecaoSintaxeIncorreta {
        int contAbreParenteses = 0;
        int contFechaParenteses = 0;
        int contNumeroGet = 0;
        int tempNumGet = 0;
        char caractere = input.get();
        while (caractere != 40) {
            if (caractere == 32) {
                caractere = input.get();
            } else {
                //exception	
            }
        }
        while (!(contFechaParenteses > contAbreParenteses)) {
            contNumeroGet++;
            caractere = input.get();
            if (caractere == 40) {
                contAbreParenteses++;
            }
            if (caractere == 41) {
                contFechaParenteses++;
            }
        }
        tempNumGet = contNumeroGet;
        while (contNumeroGet != 0) {
            input.unget();
            contNumeroGet--;
        }
        String tempPalavraLida = palavraLida + "(";
        String expressao = analiseExpressao(tempPalavraLida, (tempNumGet - 1), true);
        expressao = expressao + input.get();
        PseudoComando tempComando = new PseudoComando(null, expressao, "println", false);

        return tempComando;
    }

    private PseudoComando analiseComandoReadint(String palavraLida) {
        int contAbreParenteses = 0;
        int contFechaParenteses = 0;
        int contNumeroGet = 0;
        int tempNumGet = 0;
        String variavel = "";
        char caractere = input.get();
        while (caractere != 40) {
            if (caractere == 32) {
                caractere = input.get();
            } else {
                //exception	
            }
        }
        while (!(contFechaParenteses > contAbreParenteses)) {
            contNumeroGet++;
            caractere = input.get();
            if (caractere == 40) {
                contAbreParenteses++;
            }
            if (caractere == 41) {
                contFechaParenteses++;
            }
        }
        tempNumGet = contNumeroGet;
        while (contNumeroGet != 0) {
            input.unget();
            contNumeroGet--;
        }
        while (contNumeroGet != tempNumGet - 1) {
            variavel += input.get();
        }
        validaVariavel(variavel);
        String stringComando = palavraLida + "(";
        stringComando += variavel;
        PseudoComando tempComando = new PseudoComando(null, variavel, "readint", false);

        return tempComando;
    }

    private String verificaOperadorUnario() {
        int contaParentesesAbre = 1;
        int contaParentesesFecha = 0;
        String retorno = "";
        do {
            char caractere = input.get();

            if (caractere == '(') {
                contaParentesesAbre++;
            } else if (caractere == ')') {
                contaParentesesFecha++;
            }
            retorno += caractere;
        } while (contaParentesesAbre - contaParentesesFecha != 0);

        return retorno;
    }

    private boolean verificaAtribuicaoDepois() {
        char caractere = input.get();
        int contaGet = 0;

        contaGet++;
        while (caractere == 32) {
            caractere = input.get();
            contaGet++;
        }
        if (caractere == ':') {
            caractere = input.get();
            contaGet++;
            if (caractere == '=') {
                for (int i = 0; i < contaGet; i++) {
                    input.unget();
                }
                return true;
            } else {
                //Exception
            }
        }

        for (int i = 0; i < contaGet; i++) {
            input.unget();
        }

        return false;
    }
}
