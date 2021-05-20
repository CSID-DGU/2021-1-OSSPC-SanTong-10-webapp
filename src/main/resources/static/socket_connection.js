#!/usr/bin/env node

/**
 * Module dependencies.
 */

const app = require('../app');
const debug = require('debug')('server:server');
const http = require('http');

/**
 * Get port from environment and store in Express.
 */

const port = normalizePort(process.env.PORT || '3000');
const host = process.env.IP || 'localhost';
app.set('port', port);

/**
 * Create HTTP server.
 */

const server = http.createServer(app);
const io = require('socket.io')(server, { cors: { origin: '*' }});

let players = [];
let gameData = [];

/**
 * Listen on provided port, on all network interfaces.
 */

server.listen(port, host, function () {
    console.log(`server running at ${host}:${port}`);
});

server.on('error', onError);
server.on('listening', onListening);

// 소켓 통신 시작
io.sockets.on('connection', socket => {
    let currentPlayer = ''; // 플레이어 상태 값 저장
    // 플레이어 목록 중에 socket id랑 같은 Index 값 반환
    const findIndexPlayers = (socketId) => players.findIndex((player) => player === socketId);

    socket.emit('connecting', { players, gameData });

    // client에서 socket.id로 생성한 플레이어 고유값(id) 넘겨줌
    socket.on('ready', ({ userId }) => {
        console.log(`connect: ${userId}`);

        // 다른 플레이어 목록이 있는지 확인
        const otherPlayer = gameData.filter(d => d.player !== userId);
        let dolType = 'WHITE';

        // 플레이어의 바둑돌 색깔 정함
        if (otherPlayer.length && otherPlayer[0].player) {
            dolType = otherPlayer[0].dolType === 'WHITE' ? 'BLACK' : 'WHITE';
        }

        // Disconnect 됐을 경우 플레이어 목록에서 현재 플레이어 ID 삭제
        currentPlayer = userId;

        players.push(userId);
        socket.emit('player_dol_type', { dolType });
    });

    // 게임 데이터 초기화
    socket.on('init_data', ({ userId, dolType })  => {
        const index = findIndexPlayers(userId);
        // 접속자가 2명 이상일 때, 들어오는 사람은 게임에 참여할 수 없게 하기 위해 state 상태 다르게 넣어줌
        const state = index < 2 ? 'ready' : 'not_player';
        const turn = dolType === 'WHITE' ? true : false;

        gameData.push({
            player: userId,
            dolType,
            board: [
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
                [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
            ],
            state,
            clicked: '',
            turn
        });

        // client에 게임 데이터 전송
        socket.emit('game_data', gameData[index]);
    });

    // client의 플레이어가 돌을 클릭했을 때 호출됨
    socket.on('update_game_data', (data) => {
        const { board, userId, clicked } = data;
        const index = findIndexPlayers(userId);
        const otherPlayerIndex = {
            0: 1,
            1: 0
        }[index];
        let turn = true;

        console.log(`update game: userId(${userId}), clicked(${clicked})`);

        gameData.map((d, index2) => {
            // 돌을 놓은 플레이어의 상태를 변경
            if (index2 === index) {
                d.state = 'finish';
                turn = false;
            }

            d.board = board;
            d.clicked = clicked;
            d.turn = turn;
        });

        // 돌을 놓은 플레이어는 바둑판을 클릭할 수 없도록 해당 client에 전송
        socket.emit('not_your_turn', { player: currentPlayer });
        // 돌을 놓은 플레이어를 제외한 다른 플레이어에게 게임 데이터 전송
        socket.broadcast.emit('game_data', gameData[index]);
    });

    // 플레이어 중 한 명이 이겼을 때 호출됨
    socket.on('win', data => {
        const {
            board,
            player,
            clicked,
            dolType
        } = data;

        console.log(`win player: ${player}`);

        // 게임 데이터 업데이트
        gameData.map(d => d.board = board);

        // 다른 플레이어에게 이긴 플레이어의 정보와 click한 바둑알 정보 전송
        socket.broadcast.emit('won', {
            winner: player,
            clicked: clicked,
            dolType: dolType
        });
    });

    // // 시간제한 기능
    // socket.on('set_timer', data => {
    //   const { dolType, player } = data;
    //   const index = findIndexPlayers(player);
    //   const playerData = gameData[index];
    //   let seconds = data.seconds;

    //   console.log('set timer ', index);

    //   let timer = setInterval(() => {
    //     // 플레이어 상태가 ready 일 때
    //     if (playerData.state === 'finish') {
    //       const otherPlayerIndex = {
    //         0: 1,
    //         1: 0
    //       }[index];
    //       gameData[otherPlayerIndex].turn = true;
    //       gameData[otherPlayerIndex].state = 'ready';

    //       socket.broadcast.emit('game_data', gameData[otherPlayerIndex]);
    //       clearInterval(timer);
    //       return;
    //     }

    //     // 시간 안에 바둑알이 놓아졌을 경우
    //     if (dolType !== playerData.dolType) {
    //       clearInterval(timer);
    //     }

    //     // 타이머가 0보다 크면
    //     if (seconds > 0 && playerData.turn === true) {
    //       seconds--;
    //       playerData.timer = seconds;

    //       socket.emit('timer_checker', playerData.timer);

    //       if (playerData.timer === 0) {
    //         playerData.state = 'finish';
    //         playerData.turn = false;
    //         socket.emit('game_data', playerData);
    //       }
    //     } else {
    //       clearInterval(timer);
    //     }
    //   }, 1000);
    // })

    socket.on('disconnect', () => {
        const index = findIndexPlayers(currentPlayer);
        console.log('user disconnect: ', currentPlayer, index);
        players.splice(index, 1);
        gameData.splice(index, 1);
    })
});

/**
 * Normalize a port into a number, string, or false.
 */

function normalizePort(val) {
    const port = parseInt(val, 10);

    if (isNaN(port)) {
        // named pipe
        return val;
    }

    if (port >= 0) {
        // port number
        return port;
    }

    return false;
}

/**
 * Event listener for HTTP server "error" event.
 */

function onError(error) {
    if (error.syscall !== 'listen') {
        throw error;
    }

    const bind = typeof port === 'string'
        ? 'Pipe ' + port
        : 'Port ' + port;

    // handle specific listen errors with friendly messages
    switch (error.code) {
        case 'EACCES':
            console.error(bind + ' requires elevated privileges');
            process.exit(1);
            break;
        case 'EADDRINUSE':
            console.error(bind + ' is already in use');
            process.exit(1);
            break;
        default:
            throw error;
    }
}

/**
 * Event listener for HTTP server "listening" event.
 */

function onListening() {
    const addr = server.address();
    const bind = typeof addr === 'string'
        ? 'pipe ' + addr
        : 'port ' + addr.port;
    debug('Listening on ' + bind);

    console.log(addr)
}