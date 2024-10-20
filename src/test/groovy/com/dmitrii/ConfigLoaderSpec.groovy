package com.dmitrii

import spock.lang.Specification

class ConfigLoaderSpec extends Specification {

    def "loadConfig successfully loads valid config file"() {
        given:
        def validConfigFilePath = 'src/test/resources/config.json'

        when:
        def config = ConfigLoader.loadConfig(validConfigFilePath)

        then:
        config.columns == 4
        config.rows == 4

        config.symbols.size() == 11
        config.symbols['A'].rewardMultiplier == 5
        config.symbols['B'].rewardMultiplier == 3
        config.symbols['C'].rewardMultiplier == 2.5
        config.symbols['D'].rewardMultiplier == 2
        config.symbols['E'].rewardMultiplier == 1.2
        config.symbols['F'].rewardMultiplier == 1
        config.symbols['10x'].rewardMultiplier == 10
        config.symbols['5x'].rewardMultiplier == 5
        config.symbols['+1000'].extra == 1000
        config.symbols['+500'].extra == 500
        config.symbols['MISS'].type == 'bonus'
        config.symbols['MISS'].impact == 'miss'

        config.probabilities.standardSymbols.size() == 9
        config.probabilities.bonusSymbols.symbols['10x'] == 1
        config.probabilities.bonusSymbols.symbols['5x'] == 2
        config.probabilities.bonusSymbols.symbols['+1000'] == 3
        config.probabilities.bonusSymbols.symbols['+500'] == 4
        config.probabilities.bonusSymbols.symbols['MISS'] == 5

        config.winCombinations.size() == 11
        config.winCombinations['same_symbol_3_times'].rewardMultiplier == 1
        config.winCombinations['same_symbol_3_times'].count == 3
        config.winCombinations['same_symbols_diagonally_right_to_left'].coveredAreas == [['0:2', '1:1', '2:0']]

    }
}
