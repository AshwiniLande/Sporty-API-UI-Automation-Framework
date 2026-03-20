Feature: Twitch mobile search

  @UI
  Scenario: Search StarCraft II streamer

    Given user opens Twitch website
    When user clicks search icon
    And user searches for "StarCraft II"
    And user scrolls down 2 times
    And user selects a streamer
    Then streamer page should load with text "StarCraft II"
    And screenshot should be taken