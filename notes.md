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
Client -> Server: [PUT] /game\nauthToken\n{ "playerColor":"WHITE/BLACK", "gameID": 1234 }
Server -> Handler: { "playerColor":"WHITE/BLACK", "gameID": 1234 }
Handler -> Service: games(JoinRequest)
Service-> DataAccess: getSession(authToken)
DataAccess -> db:Find authData by authToken
DataAccess --> Service: authData
Service-> DataAccess: joinGame(username, gameID, color)
DataAccess -> db:Update gameData by gameID
DataAccess --> Service: game
Service --> Handler: {}
Handler --> Server: {}
Server --> Client: 200\n{}
end

group #gray Clear application #white
Client -> Server: [DELETE] /db
Server -> Handler: {}
Handler -> Service: db(Clear)
Service-> DataAccess: users(Clear)
DataAccess -> db:Clear users
Service-> DataAccess: authTokens(Clear)
DataAccess -> db:Clear authTokens
Service-> DataAccess: games(Clear)
DataAccess -> db:Clear games
DataAccess --> Service: cleared
Service --> Handler: cleared
Handler --> Server: {}
Server --> Client: 200\n{}
end
