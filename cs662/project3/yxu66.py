from __future__ import division
from copy import deepcopy
import gameplay
import sys


## Evaluate the board according to the base value of all
def value(board):
    evalvalue = 0
    baseValue = [[99, -8, 8, 6, 6, 8, -8, 99], [-8, -24, -4, -3, -3, -4, -24, -8], [8, -4, 7, 4, 4, 7, -4, 8], [6, -3, 4, 0, 0, 4, -3, 6], [6, -3, 4, 0, 0, 4, -3, 6], [8, -4, 7, 4, 4, 7, -4, 8],[-8, -24, -4, -3, -3, -4, -24, -8], [99, -8, 8, 6, 6, 8, -8, 99]]

    if board[0][0] == 'B':
        baseValue[0][1] = baseValue[1][0] = 40
    elif board[0][0] == 'W' :
        baseValue[0][1] = baseValue[1][0] = -40
    if board[0][7] == 'B':
        baseValue[0][6] = baseValue[1][7] = 40
    elif board[0][7] == 'W':
        baseValue[0][6] = baseValue[1][7] = -40
    if board[7][0] == 'B':
        baseValue[7][1] = baseValue[6][0] = 40
    elif board[7][0] == 'W' :
        baseValue[7][1] = baseValue[6][0] = -40
    if board[7][7] == 'B':
        baseValue[7][6] = baseValue[6][7] = 40
    elif board[7][7] == 'W':
        baseValue[7][6] = baseValue[6][7] = -40

    blackNum = gameplay.score(board)[0]
    whiteNum = gameplay.score(board)[1]
    if (blackNum != 0) & (whiteNum != 0):
        flipValueB = blackNum / whiteNum
        flipValueW = whiteNum / blackNum
    elif blackNum == 0:
        flipValueW = 100
    elif whiteNum == 0:
        flipValueB = 100

    for i in range(8):
        for j in range(8):
            if board[i][j] == 'B':
                evalvalue = evalvalue + baseValue[i][j] * flipValueB
            elif board[i][j] == 'W':
                evalvalue = evalvalue - baseValue[i][j] * flipValueW

    return evalvalue

def betterThan(val1, val2, color, reversed):
    if color == "B":
        isBetter = val1 > val2
    else:
        isBetter =  val2 > val1
    if reversed:
        return not isBetter
    else:
        return isBetter

def alphaBeta(board, color, reversed, depth, alpha = -sys.maxint, beta = sys.maxint):

    moves = []
    for i in range(8) :
        for j in range(8) :
            if gameplay.valid(board, color, (i,j)) :
                moves.append((i,j))

    if depth == 0:
        return value(board)

    if len(moves) == 0:
        if (gameplay.gameOver(board)):
            return value(board)
        else:
            moves.insert(0, 'pass')
    if (color == "B" and not reversed) or (color == "W" and reversed) :
        for move in moves :
            newBoard = deepcopy(board)
            gameplay.doMove(newBoard,color,move)
            alpha = max(alpha, alphaBeta(newBoard, gameplay.opponent(color), reversed, depth - 1, alpha, beta))

            if beta <= alpha :
                break

        return alpha
    else :
        for move in moves :
            newBoard = deepcopy(board)
            gameplay.doMove(newBoard,color,move)
            beta = min(beta, alphaBeta(newBoard, gameplay.opponent(color), reversed, depth - 1, alpha, beta))

            if beta <= alpha :
                break

        return beta

def nextMove(board, color, time, reversed = False):

    moves = []
    for i in range(8) :
        for j in range(8) :
            if gameplay.valid(board, color, (i,j)) :
                moves.append((i,j))

    if len(moves) == 0:
        return 'pass'

    if len(moves) > 8:
        depth = 3
    elif len(moves) < 6:
        depth = 5
    else:
        depth = 4


    if time < 60:
        depth = 3
    elif time < 40:
        depth = 2

    best = None
    for move in moves:
        newBoard = deepcopy(board)
        gameplay.doMove(newBoard,color,move)
        moveVal = alphaBeta(newBoard, gameplay.opponent(color), reversed, depth)
        if best == None or betterThan(moveVal, best, color, reversed):
            bestMove = move
            best = moveVal
    return  bestMove

def nextMoveR(board, color, time):
    return nextMove(board, color, time, True)