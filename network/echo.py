from flask import Blueprint, render_template
from flask_sock import Sock

sock = Sock()
echo = Blueprint('echo', __name__,
    template_folder='templates',
    static_folder='static', 
    static_url_path='assets')

def register_page(app):
    app.register_blueprint(echo, url_prefix='/echo')
    sock.init_app(app)


@echo.route('/')
def index():
    return render_template('index.html')



@sock.route('/ws',bp=echo)
def echo(sock):
    while True:
        data = sock.receive()
        sock.send(data)


