## Projeto de computação gráfica UFRPE 2021.1 - Henrique Sabino

A primeira parte deste projeto consiste em receber arquivos que descrevem vértices e triângulos de um objeto 3D e um arquivo com configurações de uma câmera virtual para que seja possível fazer a projeção dos pontos do objeto na tela por meio de transformações matriciais.
## Como rodar o executável

Para rodar a aplicação é necessário possuir JRE instalado e baixar a última versão disponível em [releases](https://github.com/HenriqueSabino/ComputacaoGrafica_Projeto/releases) aqui no github do projeto. Na pasta do executável deve haver apenas um arquivo `.byu` e apenas um arquivo `.cam`.

## Como rodar o código

Para rodar o código é necessário possuir alguma JDK instalada (8 ou superior) e possuir a biblioteca core do Processing 3 dentro da pasta lib (no mesmo nível que a pasta src). Na pasta do projeto, assim como no github, deve haver apenas um arquivo `.byu` e apenas um arquivo `.cam`.

## Arquivo .byu

O arquivo `.byu` possui a descrição do objeto que será mostrado na tela, e possui o seguinte formato:

```
<n° vértices> <n° de triângulos>
<coordenada x do vértice 1> <coordenada y do vértice 1> <coordenada z do vértice 1>
<coordenada x do vértice 2> <coordenada y do vértice 2> <coordenada z do vértice 2>

...

<coordenada x do vértice n> <coordenada y do vértice n> <coordenada z do vértice n>
<índice do vértice 1 do triângulo 1> <índice do vértice 2 do triângulo 1> <índice do vértice 3 do triângulo 1>
<índice do vértice 1 do triângulo 2> <índice do vértice 2 do triângulo 2> <índice do vértice 3 do triângulo 2>

...

<índice do vértice 1 do triângulo k> <índice do vértice 2 do triângulo k> <índice do vértice 3 do triângulo k>
```

## Arquivo .cam

O arquivo `.cam` possui as configurações da câmera virtua, e possui o seguinte formato:

```
<coordenada x do ponto C> <coordenada y do ponto C> <coordenada z do ponto C>
<coordenada x do vetor N> <coordenada y do vetor N> <coordenada z do vetor N>
<coordenada x do vetor V> <coordenada y do vetor V> <coordenada z do vetor V>
<valor de hx> <valor de hy>
<valor de d>
```

> NOTA: É importante ressaltar que no final dos arquivos `.byu` e `.cam` deve haver uma linha em branco para que o projeto funcione corretamente.

## Alterando os arquivos .byu e .cam

É possível alterar os arquivos `.byu` e `.cam` com o projeto rodando. Para que as mudanças sejam refletidas visualmente na tela é necessário clicar na tela e precionar a tecla `R`, para que o projeto recarregue os arquivos `.byu` e `.cam`.
> NOTA: não é necessário que os arquivos modificados possuam o mesmo nome que o arquivo anterior.
