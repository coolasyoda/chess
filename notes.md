actor Client
participant Server
participant Handler
participant Service
participant DataAccess
database db

entryspacing 0.9
group #navy Registration #white
Client -> Server: [POST] /user\n{"username":" ", "password":" ", "email":" "}
Server -> Handler: {"username":" ", "password":" ", "email":" "}
Handler -> Service: register(RegisterRequest)
Service -> DataAccess: getUser(username)
DataAccess -> db:Find UserData by username
DataAccess --> Service: null
Service -> DataAccess:createUser(userData)
DataAccess -> db:Add UserData
Service -> DataAccess:createAuth(authData)
DataAccess -> db:Add AuthData
Service --> Handler: RegisterResult
Handler --> Server: {"username" : " ", "authToken" : " "}
Server --> Client: 200\n{"username" : " ", "authToken" : " "}
end

group #orange Login #white
Client -> Server: [POST] /session\n{ "username":"", "password":"" }

Server -> Handler: { "username":"", "password":"" }
Handler -> Service: login(LoginRequest)
Service-> DataAccess: getUser(username)
DataAccess -> db:Find UserData by username
DataAccess --> Service: userData
Service -> Service:loginUser(userData, password)
Service -> DataAccess:createAuth(authData)
DataAccess -> db:Add AuthData

Service --> Handler: LoginResult
Handler --> Server: { "username":"", "authToken":"" }
Server --> Client: 200\n{ "username":"", "authToken":"" }

end

group #green Logout #white
Client -> Server: [DELETE] /session\nauthToken
Server -> Handler: {"authToken":" "}
Handler -> Service: delete(SessionRequest)
Service-> DataAccess: getSession(authToken)
DataAccess -> db:Find authData by authToken
DataAccess --> Service: authData
Service -> DataAccess:deleteSession(authData)
DataAccess -> db:Remove authToken
Service --> Handler: LogoutResult
Handler --> Server: {}
Server --> Client: 200\n{}
end

group #red List Games #white
Client -> Server: [GET] /game\nauthToken
Server -> Handler: {"authToken":" "}
Handler -> Service: games(ListRequest)
Service-> DataAccess: getSession(authToken)
DataAccess -> db:Find authData by authToken
DataAccess --> Service: authData
Service-> DataAccess: getGames()
DataAccess -> db:Get Games
DataAccess --> Service: Collection<game>
Service --> Handler: GamesList
Handler --> Server: { "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}
Server --> Client: 200\n{ "games": [{"gameID": 1234, "whiteUsername":"", "blackUsername":"", "gameName:""} ]}

end

group #purple Create Game #white
Client -> Server: [POST] /game\nauthToken\n{ "gameName":"" }
Server -> Handler: { "gameName":"" }
Handler -> Service: games(NewRequest)
Service-> DataAccess: getSession(authToken)
DataAccess -> db:Find authData by authToken
DataAccess --> Service: authData
Service-> DataAccess: getGameData(gameName)
DataAccess -> db:Get gameData by gameName
DataAccess --> Service: null
Service -> DataAccess:newGame(gameName)
DataAccess -> db:Add gameData

Service --> Handler: game
Handler --> Server: { "gameID": 1234 }
Server --> Client: 200\n{ "gameID": 1234 }
end

group #yellow Join Game #black
Client -> Server: [PUT] /game\nauthToken\n{playerColor, gameID}
Server -> Handler: {"authToken":" ", "gameID":" "}
Handler -> Service: games(JoinRequest)
Service-> DataAccess: getSession(authToken)
DataAccess -> db:Find authData by authToken
DataAccess --> Service: authData
Service-> DataAccess: joinGame(authToken, gameID)
DataAccess -> db:Get gameID by authToken
DataAccess --> Service: game
Service --> Handler: {}
Handler --> Server: {}
Server --> Client: 200\n{}
end

group #gray Clear application #white
Client -> Server: [DELETE] /db
Server -> Handler: {}
Handler -> Service: db(Clear)
Service-> DataAccess: db(Clear)





https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIdTUWjFZfF5vD59SYHdsgoU+Ylj0r7lqOH5PF+SwfjAALnAWspqig5QIAePKwvuh6ouisTYgmKAumGbpkhSBq0uBowmkS4YWhy3K8gagrCjAb5iK60pJpeyHlEa2iOs63FOvKaGHuRfHyLM0HvPhcqGERNEkZ63pBgGQYhhx5qRhy0YwLGwb8QJibJghJZYTy2a5pgwEgkhJRXGBI7vhMXzTrOzYuXBAGdtkPYwP2g69I5twQS5NZBu586ef+y6rt4fiBF4KDoHuB6+Mwx7pJkmC+ReRTUNe0gAKK7kV9RFc0LQPqoT7dG5jboN5JlAiBfT1XOux9PBLWAflwkoUYKDcJksIUI+iQ4RisnIQpTIkTA5JgGNNWJGpdYNWg1FzZxhSWgxtrTsxYTtY1mkRnZ4IwId-EERqxEsqUHBDRSKDLU+a0GfIW1mhGu0cpEwwQDQV0RRtRnyc1qblFh6WWQgeambZQkgQcxZI12ORgH2A5DkunBxeugSQrau7QjAADio6splp45eezB2de5NlZV9ijnVoNzk1QGI1cbWcx5i7WYjbIEWSVOYdCk14eDs0-aRS0su9J2baGik7eyXI8gd6naEKx0C2gavbVpF3yiDcY3XJcvhgrlOjKosLfbR2mlOTFIU1Txs-Z2YvwBAyooOAT4ADxs6M+Tg52NnlPbaik7Ews9b7V5pqBfTh2o4yVP0mcAJLSNnACMvYAMwACxPCemQGhWYVTDoCCgA2tehb+TyZwAcqOLl7DAjSo6nPn09jgXp5nqjZxUuejgXxdl5XUzV-qlH3O3DdNyALer5BUxdz3v59wPpixZ48UbtgPhQNg3DwLqmSe6MKRZWemOi6n5Q3g0rPs8EhsvhnUc3dnK-i8mjNkMdSx7yAW3P8SdUzvx4gtCWncUDJGlliWWBJ7qkgWhSN6q0VbOzdHRLWvJrryH1hbGcYMzopyQRQ4AWCZA4PdHgsAcdGSwn3lRb2Ls-puw9pnI6yDRjAPYqw+h-UFRKhVLdG2JE0BoLjtwmBvC6Gu10sIwy8jIbAnFuKGyUiQLQNGHPcoJcK5gKHlxDGfkAoAPzoXCxC8vKnzXAlAIlghpoWSDAAAUhAHkj9DABEbs3Omb9GZpmqJSO8LRM4c3WnOIcN9gDeKgHAAO0BZhOMHgVXqkD+gq06h3WezihZGLNgNCesJAkWRQGiKazCzoKwIWgD6kViEaz2trahIiVZ8JIbYv2jCWmsPKAAKyCWgFRKtcnlO6VpAR7tmBOKoYMjR1SDFiF0TzHqVxuoIJGR-Us+Tji2NyljfyONeh4xXGfQmAQvDpK7F6WAwBsA30IPERIz9aZXMQfZSoxVSrlUqsYbmZwDlnPgYhISl1jCwkQO86a-UFEPXZMi7geAlm-U1iivA7IhmcW2TAEAOLiZR1sZAil7yUAriqdEw5TVThXNHrjMw+MgA
DataAccess -> db:Clear db
DataAccess --> Service: cleared
Service --> Handler: cleared
Handler --> Server: {}
Server --> Client: 200\n{}
end
