import requests
import json
import pprint
from requests.exceptions import ReadTimeout

logic = 'main'


def create_url(method_name):
    token = '759868573:AAECvuI9D88aJtbXApWjFNQ1eyJAVjAgYQU'
    host = 'https://api.telegram.org'
    uri = '/bot{token}/{method}'
    url = host + uri.format(token=token, method=method_name)
    return url


def send_message(text, chat_id):
    url = create_url('sendMessage')
    settings = {'chat_id': chat_id, 'text': text, 'parse_mode': 'HTML'}
    response = requests.post(url, settings)
    response_content = json.loads(response.content.decode('utf-8'))
    pprint.pprint(response_content)


def get_updates(update_id=None):
    url = create_url('getUpdates')
    if not update_id:
        settings = {'timeout': 10}
        response = requests.post(url, settings, timeout=10)
        response_content = json.loads(response.content.decode('utf-8'))
    else:
        settings = {'offset': update_id, 'timeout': 10}
        response = requests.post(url, settings, timeout=10)
        response_content = json.loads(response.content.decode('utf-8'))
    return response_content


def translate(text):
    settings = \
        {
            'key': 'trnsl.1.1.20190808T125219Z.d8ac65be89fdf70f.ac4fe05721232e413876341d1d68e01cf7bd526b',
            'text': text,
            'lang': 'ru',
            'format': 'plain',
            'options': '1'
        }
    response = requests.post('https://translate.yandex.net/api/v1.5/tr.json/translate', settings)
    response_content = json.loads(response.content.decode('utf-8'))
    if response_content['code'] == 200:
        return response_content['text'][0]
    elif response_content['code'] == 404:
        return 'Превышено суточное ограничение на объем переведенного текста'
    elif response_content['code'] == 413:
        return 'Превышен максимально допустимый размер текста'
    elif response_content['code'] == 422:
        return 'Текст не может быть переведен'
    else:
        return 'Произошла непредвиденная ошибка'


def define_lang(text):
    settings = \
        {
            'key': 'trnsl.1.1.20190808T125219Z.d8ac65be89fdf70f.ac4fe05721232e413876341d1d68e01cf7bd526b',
            'text': text
        }
    response = requests.post('https://translate.yandex.net/api/v1.5/tr.json/detect', settings)
    response_content = json.loads(response.content.decode('utf-8'))
    pprint.pprint(response_content['lang'])
    return response_content['lang']


def get_langs(lang):
    settings = \
        {
            'key': 'trnsl.1.1.20190808T125219Z.d8ac65be89fdf70f.ac4fe05721232e413876341d1d68e01cf7bd526b',
            'ui': lang
        }
    response = requests.post('https://translate.yandex.net/api/v1.5/tr.json/getLangs', settings)
    response_content = json.loads(response.content.decode('utf-8'))
    return response_content['langs']


def get_messages():
    next_update_id = None

    while True:
        try:
            result = get_updates(next_update_id)
        except ReadTimeout:
            continue

        for update in result.get('result', []):
            if 'message' in update:
                pprint.pprint(update['message'])
                yield update['message']
            else:
                pass

        if result.get('result'):
            next_update_id = result['result'][-1].get('update_id') + 1


def price_bitcoin():
    text = requests.get('https://coinmarketcap.com/currencies/bitcoin/')
    text_content = bytes.decode(text.content)
    find = '<span id="quote_price" data-currency-price data-usd="'
    text_find_start = text_content.find(find) + len(find)
    text_find = ''
    while text_content[text_find_start] != '"':
        text_find = text_find + text_content[text_find_start]
        text_find_start = text_find_start + 1
    try:
        price = float("{0:.2f}".format(float(text_find)))
        return price
    except TypeError:
        return 'Что-то пошло не так'


for message in get_messages():
    if logic == 'main':
        if message['text'] == '/help':
            send_message(
                'Эти сообщения я понимаю:\n'
                '/start - стартовое сообщение\n'
                '/help - списков всех команд\n'
                '/translate - перевод текста на русский язык\n'
                '/bitcoin - курс биткойна',
                message['chat']['id'])
        elif message['text'] == '/start':
            send_message('Привет, напиши /help', message['chat']['id'])
        elif message['text'] == '/translate':
            send_message('Введите текст для перевода (макс 4000 символов)', message['chat']['id'])
            logic = 'translate'
        elif message['text'] == '/bitcoin':
            send_message('<b>1 btc = {} usd</b>\n'
                         'Данные с сайта\n'
                         '<a href="https://coinmarketcap.com/">CoinMarketCap</a>'.format(price_bitcoin()), message['chat']['id'])
        else:
            send_message('Я рад, что вы общаетесь со мной, но я всего лишь машина, которая не понимает, что '
                         'вы написали, поэтому напишите /help', message['chat']['id'])
    elif logic == 'translate':
        send_message('<b>Определённый язык:</b> {}\n'
                     'Перевод:\n\n'
                     '{}\n\n'
                     'Перевод выполнил:\n'
                     '<a href="https://translate.yandex.ru/">Яндекс.Переводчик</a>'
                     .format(define_lang(message['text']), translate(message['text'])), message['chat']['id'])
        logic = 'main'
    else:
        pass

