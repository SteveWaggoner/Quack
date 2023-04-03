from flask import Blueprint, render_template


echo = Blueprint('echo', __name__,
    template_folder='templates',
    static_folder='static', 
    static_url_path='assets')

@echo.route('/')
def index():
    return '<h1>Echo page</h1>'

