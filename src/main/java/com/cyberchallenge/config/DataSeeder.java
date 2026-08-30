package com.cyberchallenge.config;

import com.cyberchallenge.model.Pergunta;
import com.cyberchallenge.repository.PerguntaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BUG CORRIGIDO: o projeto original nao tinha nenhuma pergunta cadastrada em
 * lugar nenhum (nem migration, nem seed). Isso significa que
 * GET /api/partidas/iniciar sempre retornaria uma lista vazia e o jogo nunca
 * comecaria de fato.
 *
 * O seed roda apenas se a tabela estiver vazia, entao e seguro reiniciar a
 * aplicacao varias vezes durante a atividade sem duplicar perguntas.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final PerguntaRepository perguntaRepository;

    public DataSeeder(PerguntaRepository perguntaRepository) {
        this.perguntaRepository = perguntaRepository;
    }

    @Override
    public void run(String... args) {
        if (perguntaRepository.count() > 0) {
            return;
        }

        perguntaRepository.saveAll(List.of(
            pergunta("Utilizar a mesma senha em varios servicos e uma pratica segura desde que a senha seja complexa.",
                "Senhas", false,
                "Reutilizar senhas aumenta o risco de comprometimento de varias contas caso uma delas seja invadida, mesmo sendo uma senha complexa."),

            pergunta("Bancos costumam enviar links por SMS pedindo a atualizacao imediata da senha para evitar o bloqueio da conta.",
                "Phishing", false,
                "Bancos nao enviam links solicitando senha por SMS. Essa e uma tecnica classica de phishing/smishing."),

            pergunta("A autenticacao de dois fatores (2FA) adiciona uma camada extra de seguranca, exigindo mais do que apenas a senha.",
                "Autenticacao", true,
                "O 2FA combina algo que voce sabe (senha) com algo que voce tem (celular/token), dificultando invasoes mesmo com a senha vazada."),

            pergunta("Navegar em uma aba anonima impede que o provedor de internet e a rede local saibam quais sites voce visita.",
                "Navegacao Segura", false,
                "A aba anonima apenas evita salvar o historico no proprio computador. O provedor de internet e a rede ainda podem ver o trafego."),

            pergunta("Atualizacoes de software sao importantes porque frequentemente corrigem vulnerabilidades recem-descobertas.",
                "Dispositivos", true,
                "Manter sistemas atualizados e uma das principais defesas contra ataques que exploram falhas ja conhecidas."),

            pergunta("Receber uma ligacao de alguem se passando por suporte tecnico pedindo acesso remoto ao computador e uma pratica legitima e comum.",
                "Engenharia Social", false,
                "Esse e um golpe classico de engenharia social. Suporte tecnico legitimo raramente liga sem voce ter aberto um chamado antes."),

            pergunta("Um anexo de e-mail inesperado, mesmo de um remetente conhecido, pode conter malware.",
                "Malware", true,
                "Contas legitimas podem ser comprometidas e usadas para espalhar malware, por isso anexos inesperados merecem desconfianca mesmo de contatos conhecidos."),

            pergunta("Compartilhar a localizacao em tempo real publicamente nas redes sociais nao traz riscos de privacidade.",
                "Privacidade", false,
                "Compartilhar localizacao em tempo real publicamente pode expor rotinas e ausencias, sendo um risco de seguranca fisica e privacidade."),

            pergunta("Verificar o remetente e o dominio de um e-mail antes de clicar em links ajuda a identificar tentativas de phishing.",
                "Segurança de E-mail", true,
                "Dominios levemente alterados (ex: 'bancoo.com' em vez de 'banco.com') sao um sinal comum de phishing."),

            pergunta("Redes Wi-Fi publicas abertas sao tao seguras quanto a rede da sua casa para acessar dados sensiveis.",
                "Navegacao Segura", false,
                "Redes Wi-Fi publicas abertas facilitam a interceptacao de dados por terceiros na mesma rede."),

            pergunta("Fazer backup regular dos dados ajuda a reduzir o impacto de um ataque de ransomware.",
                "Proteção de Dados", true,
                "Com backups atualizados e desconectados da rede principal, e possivel recuperar os dados sem pagar resgate a um atacante."),

            pergunta("Usar a mesma senha do e-mail pessoal em sites de compras online e uma pratica recomendada para nao esquecer.",
                "Senhas", false,
                "Reutilizar a senha do e-mail em outros servicos e especialmente perigoso, pois o e-mail costuma ser usado para recuperar acesso a outras contas."),

            pergunta("Mensagens que criam senso de urgencia, como 'sua conta sera bloqueada em 1 hora', sao uma tatica comum de phishing.",
                "Phishing", true,
                "Criar urgencia e uma tatica usada para impedir que a vitima pense com calma e verifique se a mensagem e legitima."),

            pergunta("Instalar aplicativos apenas de lojas oficiais reduz o risco de instalar malware no celular.",
                "Dispositivos", true,
                "Lojas oficiais possuem processos de verificacao que dificultam (embora nao eliminem) a distribuicao de aplicativos maliciosos."),

            pergunta("Perguntas de seguranca com respostas faceis de descobrir em redes sociais, como 'nome do seu primeiro animal de estimacao', sao totalmente seguras.",
                "Autenticacao", false,
                "Informacoes pessoais publicadas em redes sociais podem ser usadas por atacantes para responder perguntas de seguranca e recuperar contas indevidamente."),

            pergunta("Um cadeado no navegador (HTTPS) garante que o site e totalmente confiavel e nunca e falso.",
                "Navegacao Segura", false,
                "O HTTPS garante apenas que a conexao e criptografada, nao que o site seja legitimo. Sites falsos tambem podem usar HTTPS."),

            pergunta("Compartilhar sua senha com um colega de trabalho de confianca para resolver um problema pontual e uma pratica segura.",
                "Senhas", false,
                "Senhas sao pessoais e intransferiveis. Compartilhar, mesmo com alguem de confianca, quebra a rastreabilidade e aumenta o risco de vazamento."),

            pergunta("Golpes de engenharia social podem ocorrer por telefone, e-mail, mensagens de texto ou ate presencialmente.",
                "Engenharia Social", true,
                "Engenharia social explora a confianca humana e pode acontecer por qualquer canal de comunicacao, nao apenas digital.")
        ));
    }

    private Pergunta pergunta(String texto, String tema, boolean respostaCorreta, String explicacao) {
        Pergunta p = new Pergunta();
        p.setTexto(texto);
        p.setTema(tema);
        p.setRespostaCorreta(respostaCorreta);
        p.setExplicacao(explicacao);
        p.setAtiva(true);
        return p;
    }
}
