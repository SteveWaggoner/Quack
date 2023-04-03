from flask import Blueprint, render_template


def register_page(app):
    app.register_blueprint(echo, url_prefix='/echo')

    global sock
    sock = Sock(app)

echo = Blueprint('echo', __name__,
    template_folder='templates',
    static_folder='static', 
    static_url_path='assets')

@echo.route('/')
def index():
    return render_template('index.html')



@sock.route('/ws')
def echo(sock):
    while True:
        data = sock.receive()
        sock.send(data)


