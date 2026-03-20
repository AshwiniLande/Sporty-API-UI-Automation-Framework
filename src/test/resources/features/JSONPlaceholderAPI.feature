  @API
  Feature: JSONPlaceholder API Automation

  #Creating a post and storing the generated id for later use in other scenarios
    Scenario: Create a new post
      Given user adds "CreatePost" request payload with format "JSON"
        | userId | 2             |
        | title  | My First Post |
        | body   | Hello World   |
    When user hits "POSTS" resource with http method "POST"
    Then verify status code is 201
    And  store "id" as "responseId" in global memory

  #JSONPlaceholder does not actually save the data, so trying to fetch the same id will return 404
    Scenario: Get post using stored id should return 404
      Given user has "responseId" stored in global memory
      When user hits "GETPOST" resource with http method "GET"
      Then verify status code is 404

  # Deleting the same id - API still returns 200 even if data is not really present
    Scenario: Delete post
      Given user has "responseId" stored in global memory
      When user hits "REMOVE" resource with http method "DELETE"
      Then verify status code is 200

  # Idempotency Check: Deleting same resource again
      When user hits "REMOVE" resource with http method "DELETE"
      Then verify status code is 200

  # Validates API contract (status, headers) and performance SLAs
    Scenario: Validate API contract and performance for GET post
      Given user want to get data for id "7" in global memory as "responseId"
      When user hits "GETPOST" resource with http method "GET"
      Then verify status code is 200
      And verify response header "Content-Type" contains "application/json"
      And verify response time is less than 2000 ms

  # Validates correctness of response payload data against expected values
    Scenario: Validate response data integrity for GET post
      Given user want to get data for id "7" in global memory as "responseId"
      When user hits "GETPOST" resource with http method "GET"
      Then verify status code is 200
      And verify response field "id" is "7"
      And verify response field "userId" is "1"
      And verify response field "title" is "magnam facilis autem"
      And verify response field "body" is:
       """
       dolore placeat quibusdam ea quo vitae
       magni quis enim qui quis quo nemo aut saepe
       quidem repellat excepturi ut quia
       sunt ut sequi eos ea sed quas
      """

  # Checking response structure for list API using schema validation
    Scenario: Validate schema for GET all posts
      When user hits "GETALLPOSTS" resource with http method "GET"
      Then verify status code is 200
      And verify response matches "PostListSchema"
