import sys
import ast2json
import os


# Возвращает тексты .py файлов
def traverse(targetPath):
    result = []

    if os.path.isfile(targetPath):
        if not targetPath.endswith('.py'):
            return result

        with open(targetPath, 'r') as f:
            content = f.read()
            result.append({"path": targetPath, "text": content})
            return result

    for root, subdirs, files in os.walk(targetPath):

        for filename in files:
            if not filename.endswith('.py'):
                continue

            filePath = os.path.join(root, filename)
            with open(filePath, 'r') as f:
                content = f.read()
                result.append({"path": filePath, "text": content})

    return result


# Вообщем astV2 парсит Pyhton2
# astV3 - Python3
# вот так как-то
if __name__ == '__main__':
    path = sys.argv[1]

    files = traverse(path)

    result = '[\n' + ',\n'.join(list(map(str, map(lambda f: {"path": f["path"], "ast": ast2json.str2json(f["text"])}, files)))) + '\n]'
    print(result)
