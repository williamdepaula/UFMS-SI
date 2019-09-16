package william.de.paula;

public class Main {

    public static void main(String[] args) {
        Mesa mesa = new Mesa();
        for (int filosofo = 0; filosofo < Mesa.NR_FILOSOFOS; filosofo++){
            new Filosofo("Filosofo_"+filosofo, mesa, filosofo).start();
        }
    }
}

class Mesa {
    final static int PENSANDO = 1;
    final static int COMENDO  = 2;
    final static int FOME = 3;
    final static int NR_FILOSOFOS = 5;
    final static int PRIMEIRO_FILOSOFO = 0;
    final static int ULTIMO_FILOSOFO = NR_FILOSOFOS -1;

    boolean[] hashis = new boolean[NR_FILOSOFOS];
    int[] filosofos = new int[NR_FILOSOFOS];

    public Mesa() {
        for (int i=0; i<NR_FILOSOFOS; i++){
            hashis[i] = true;
            filosofos[i] = PENSANDO;
        }
    }

    public synchronized void pegarHashis(int filosofo){
        filosofos[filosofo] = FOME;
        while (filosofos[aEsquerda(filosofo)] == COMENDO ||
                filosofos[aDireita(filosofo)] == COMENDO) {
            try {
                wait();
            } catch (InterruptedException e){

            }
        }
        hashis[hashiEsquerdo(filosofo)] = false;
        hashis[hashiDireito(filosofo)] = false;
        filosofos[filosofo] = COMENDO;
        imprimeEstadosFilosofos();
    }

    public synchronized void devolverHashis(int filosofo){
        hashis[hashiEsquerdo(filosofo)] = true;
        hashis[hashiDireito(filosofo)] = true;
        if(filosofos[aEsquerda(filosofo)] == FOME ||
                filosofos[aDireita(filosofo)] == FOME) {
            notifyAll();
        }
        filosofos[filosofo] = PENSANDO;
        imprimeEstadosFilosofos();
    }

    public int aDireita(int filosofo){
        int direito;
        if(filosofo == ULTIMO_FILOSOFO){
            direito = PRIMEIRO_FILOSOFO;
        } else {
            direito = filosofo + 1;
        }

        return direito;
    }

    public int aEsquerda(int filosofo){
        int esquerdo;
        if(filosofo == PRIMEIRO_FILOSOFO){
            esquerdo = ULTIMO_FILOSOFO;
        } else {
            esquerdo = filosofo - 1;
        }

        return esquerdo;
    }

    public int hashiEsquerdo(int filosofo){
        return filosofo;
    }

    public int hashiDireito(int filosofo){
        int hashiDireito;
        if(filosofo == ULTIMO_FILOSOFO){
            hashiDireito = 0;
        } else {
            hashiDireito = filosofo+1;
        }
        return hashiDireito;
    }

    public void imprimeEstadosFilosofos(){
        String texto = "*";
        System.out.print("Filósofos = [ ");
        for (int i=0; i<NR_FILOSOFOS; i++){
            texto = "Filosofo_"+i;
            switch (filosofos[i]){
                case PENSANDO:
                    texto += ": PENSANDO";
                    break;
                case FOME:
                    texto += ":     FOME";
                    break;
                case COMENDO:
                    texto += ":  COMENDO";
                    break;
            }
            System.out.print(texto+" ");
        }
        System.out.println("]");
    }
}


class Filosofo extends Thread {
    int filosofo;
    Mesa mesa;
    String nome;

    public Filosofo(String nome, Mesa mesa, int filosofo){
        super(nome);
        this.nome = nome;
        this.mesa = mesa;
        this.filosofo = filosofo;
    }

    public void run(){
        int tempo = 0;
        while (true){
            tempo = (int) (Math.random() * 100);
            pensar(tempo);
            pegarHashi();
            tempo = (int) (Math.random() * 100);
            comer(tempo);
            devolverHashi();
        }
    }

    public void pensar(int tempo){
        try {
            sleep(tempo);
        } catch (InterruptedException e) {
            System.out.println("Pensou d+");
        }
    }

    public void pegarHashi(){
        mesa.pegarHashis(filosofo);
    }

    public void comer(int tempo){
        try {
            sleep(tempo);
        } catch (InterruptedException e){
            System.out.println("Comeu d+");
        }
    }

    public void devolverHashi(){
        mesa.devolverHashis(filosofo);
    }

}