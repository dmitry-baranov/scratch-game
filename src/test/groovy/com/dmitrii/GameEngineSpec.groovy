package com.dmitrii

import spock.lang.Specification
import spock.lang.Unroll

class GameEngineSpec extends Specification {

    @Unroll
    @SuppressWarnings("GroovyAccessibility")
    def "generateMatrix produces expected symbols at fixed positions"() {
        given:
        def configPath = "src/test/resources/config.json"
        def config = ConfigLoader.loadConfig(configPath)

        def random = new Random(seed)
        def betAmount = 100.0
        def gameEngine = new GameEngine(config, betAmount, random)

        when:
        gameEngine.generateMatrix()
        def matrix = gameEngine.matrix

        then:
        matrix.getSymbol(0, 0).name == expectedSymbols[0][0]
        matrix.getSymbol(0, 1).name == expectedSymbols[0][1]
        matrix.getSymbol(0, 2).name == expectedSymbols[0][2]
        matrix.getSymbol(0, 3).name == expectedSymbols[0][3]
        matrix.getSymbol(1, 0).name == expectedSymbols[1][0]
        matrix.getSymbol(1, 1).name == expectedSymbols[1][1]
        matrix.getSymbol(1, 2).name == expectedSymbols[1][2]
        matrix.getSymbol(1, 3).name == expectedSymbols[1][3]
        matrix.getSymbol(2, 0).name == expectedSymbols[2][0]
        matrix.getSymbol(2, 1).name == expectedSymbols[2][1]
        matrix.getSymbol(2, 2).name == expectedSymbols[2][2]
        matrix.getSymbol(2, 3).name == expectedSymbols[2][3]
        matrix.getSymbol(3, 0).name == expectedSymbols[3][0]
        matrix.getSymbol(3, 1).name == expectedSymbols[3][1]
        matrix.getSymbol(3, 2).name == expectedSymbols[3][2]
        matrix.getSymbol(3, 3).name == expectedSymbols[3][3]

        where:
        seed || expectedSymbols
        42   || [
                ['MISS', 'C', 'A', 'D'],
                ['F', 'MISS', 'C', 'B'],
                ['5x', '+1000', 'B', 'MISS'],
                ['MISS', 'A', 'D', '+500']
        ]
        55   || [
                ['MISS', 'F', '5x', 'B'],
                ['MISS', 'F', 'B', 'E'],
                ['D', '+500', '+1000', 'F'],
                ['MISS', '+500', '+1000', 'E']
        ]
        77   || [
                ['C', '5x', 'D', 'D'],
                ['D', 'E', 'D', 'MISS'],
                ['D', '+1000', '+500', '5x'],
                ['MISS', 'MISS', 'F', 'F']
        ]
    }

    @Unroll
    def "Complete game cycle"() {
        given:
        def configPath = "src/test/resources/config.json"
        def config = ConfigLoader.loadConfig(configPath)

        def random = new Random(seed)
        def engine = new GameEngine(config, betAmount, random)

        when:
        engine.generateMatrix()
        def result = engine.evaluateResult()

        then:
        result.matrix != null
        result.reward == expectedReward

        where:
        seed || betAmount || expectedReward
        42   || 100       || 0.0D
        77   || 50        || 1200.0D
        77   || 100       || 2000.0D
        77   || 200       || 4000.0D
        9988 || 100       || 1200.0D
    }
}
