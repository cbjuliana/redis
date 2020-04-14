package redinsgo;

import java.util.List;

import redis.clients.jedis.Jedis;

public class Redinsgo {

	public static void main(String[] args) {

		try {

			Jedis jedis = new Jedis("localhost");
			System.out.println("Conectado ao redis...");
			System.out.println("");

			System.out.println("Configurando o jogo...");
			System.out.println("");		
			
			boolean bingo = false;

			// gerando números de 1 à 99 para geração de cartelas e sorteio do bingo		
			String [] numeros = new String[99];			
			for (int i = 0; i <= 98; i++) {
				numeros[i] = String.valueOf(i+1);
			}
			jedis.sadd("numeros", numeros);
			System.out.println("Números para geração das cartelas e sorteio do bingo");	
			System.out.println(jedis.smembers("numeros"));

			// criando os jogadores
			System.out.println("Criando os jogadores do bingo");
			for (int i = 1; i <= 50; i++) {

				jedis.hset("user" + i, "name", "user" + i);
				jedis.hset("user" + i, "bcartela", "cartela" + i);
				jedis.hset("user" + i, "bscore", "score" + i);

				jedis.zadd("score", 0, "score" + i);

				// criando a cartela do jogador
				List<String> numerosCartela = jedis.srandmember("numeros", 15);

				for (String numeroCartela : numerosCartela) {

					jedis.sadd("cartela" + i, numeroCartela);

				}
				System.out.println("Criada a cartela do jogador User" + i + ": " + jedis.smembers("cartela" + i));

			}

			System.out.println("");
			System.out.println("Iniciando jogo...");
			System.out.println("");

			while (bingo != true) {

				String numeroSorteado = jedis.srandmember("numeros", 1).get(0);

				jedis.srem("numeros", numeroSorteado);

				System.out.println("NÚMERO SORTEADO: " + numeroSorteado);

				// verificando se algum jogador acertou o número sorteado
				for (int i = 1; i <= 50; i++) {

					if (jedis.sismember("cartela" + i, numeroSorteado)) {

						jedis.zincrby("score", 1, "score" + i);

						System.out.println("User" + i + " ACERTOU o número: " + numeroSorteado + " SCORE: "
								+ jedis.zscore("score", "score" + i));

						// verifica se o jogador fez bingo
						if (jedis.zscore("score", "score" + i) == 15) {

							bingo = true;

							System.out.println(
									"User" + i + " fez BINGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO!!!!!!!!!");

						}

					}

				}

			}

			System.out.println("");
			System.out.println("Fim do jogo...");
			System.out.println("");

			System.out.println("Números que não foram sorteados: " + jedis.smembers("numeros"));
			
			//excluindo cartelas
			String [] cartelas = new String[50];
			for (int i = 0; i <= 49; i++) {
				cartelas[i] = "cartela" + (i+1);
			}
			jedis.del(cartelas);	

		} catch (Exception e) {
			System.out.println(e);
		}

	}

}
