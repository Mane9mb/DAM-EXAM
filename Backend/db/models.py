#!/usr/bin/python
# -*- coding: utf-8 -*-

import binascii
import datetime
import enum
import logging
import os
from _operator import and_
from builtins import getattr
from urllib.parse import urljoin

import falcon
from passlib.handlers.cisco import cisco_type7
from passlib.hash import pbkdf2_sha256
from sqlalchemy import Column, Date, DateTime, Enum, ForeignKey, Integer, Unicode, \
    UnicodeText, Table, type_coerce, case, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.ext.hybrid import hybrid_method, hybrid_property
from sqlalchemy.orm import relationship
from sqlalchemy_i18n import make_translatable

import messages
from db.json_model import JSONModel
import settings

mylogger = logging.getLogger(__name__)

SQLAlchemyBase = declarative_base()
make_translatable(options={"locales": settings.get_accepted_languages()})


def _generate_media_url(class_instance, class_attibute_name, default_image=False):
    class_base_url = urljoin(urljoin(urljoin("http://{}".format(settings.STATIC_HOSTNAME), settings.STATIC_URL),
                                     settings.MEDIA_PREFIX),
                             class_instance.__tablename__ + "/")
    class_attribute = getattr(class_instance, class_attibute_name)
    if class_attribute is not None:
        return urljoin(urljoin(urljoin(urljoin(class_base_url, class_attribute), str(class_instance.id) + "/"),
                               class_attibute_name + "/"), class_attribute)
    else:
        if default_image:
            return urljoin(urljoin(class_base_url, class_attibute_name + "/"), settings.DEFAULT_IMAGE_NAME)
        else:
            return class_attribute


def _generate_media_path(class_instance, class_attibute_name):
    class_path = "/{0}{1}{2}/{3}/{4}/".format(settings.STATIC_URL, settings.MEDIA_PREFIX, class_instance.__tablename__,
                                              str(class_instance.id), class_attibute_name)
    return class_path


class GenereEnum(enum.Enum):
    male = "M"
    female = "F"


class CategoryEnum(enum.Enum):
    db = "db"
    os = "os"
    net = "net"
    patterns = "patterns"



class AnswerQuestionAssiation(SQLAlchemyBase, JSONModel):
        __tablename__ = "answer-question-association"

        id_question = Column(Integer, ForeignKey("questions.id",
                                             onupdate="CASCADE", ondelete="CASCADE"),
                         nullable=False, primary_key=True)
        id_answer = Column(Integer, ForeignKey("answers.id",
                                                   onupdate="CASCADE", ondelete="CASCADE"),
                               nullable=False, primary_key=True)
        is_correct = Column(Boolean, nullable=False)

        answers = relationship("Answer")


class Answer(SQLAlchemyBase, JSONModel):
    __tablename__ = "answers"

    id = Column(Integer, primary_key=True)
    answer = Column(UnicodeText)

    @hybrid_property
    def json_model(self):
        return {
            "id": self.id,
            "answer": self.answer,
        }


class Question(SQLAlchemyBase, JSONModel):
    __tablename__ = "questions"

    id = Column(Integer, primary_key=True)
    question = Column(UnicodeText)
    category = Column(Enum(CategoryEnum))
    answers = relationship("AnswerQuestionAssiation")

    @hybrid_property
    def json_model(self):
        return {
            "id": self.id,
            "question": self.question,
            "category": self.category.value,
        }

class UserToken(SQLAlchemyBase):
    __tablename__ = "users_tokens"

    id = Column(Integer, primary_key=True)
    token = Column(Unicode(50), nullable=False, unique=True)
    user_id = Column(Integer, ForeignKey("users.id", onupdate="CASCADE", ondelete="CASCADE"), nullable=False)
    user = relationship("User", back_populates="tokens")


class User(SQLAlchemyBase, JSONModel):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True)
    created_at = Column(DateTime, default=datetime.datetime.now, nullable=False)
    username = Column(Unicode(50), nullable=False, unique=True)
    password = Column(UnicodeText, nullable=False)
    email = Column(Unicode(255))
    tokens = relationship("UserToken", back_populates="user", cascade="all, delete-orphan")
    name = Column(Unicode(50))
    surname = Column(Unicode(50))
    birthdate = Column(Date)
    genere = Column(Enum(GenereEnum))
    phone = Column(Unicode(50))
    photo = Column(Unicode(255))
    points = Column(Integer, default=0)
    exam = Column(UnicodeText, default="")


    @hybrid_property
    def public_profile(self):
        return {
            "created_at": self.created_at.strftime(settings.DATETIME_DEFAULT_FORMAT),
            "id": self.id,
            "username": self.username,
            "genere": self.genere.value,
            "photo": self.photo,
            "points": self.points
        }

    @hybrid_property
    def photo_url(self):
        return _generate_media_url(self, "photo")

    @hybrid_property
    def photo_path(self):
        return _generate_media_path(self, "photo")

    @hybrid_method
    def set_password(self, password_string):
        self.password = pbkdf2_sha256.hash(password_string)

    @hybrid_method
    def set_exam(self, exam_string):
        self.exam = cisco_type7.hash(exam_string)


    @hybrid_method
    def check_password(self, password_string):
        return pbkdf2_sha256.verify(password_string, self.password)

    @hybrid_method
    def get_exam(self, exam_hash):
        return cisco_type7.decode(exam_hash)

    @hybrid_method
    def create_token(self):
        if len(self.tokens) < settings.MAX_USER_TOKENS:
            token_string = binascii.hexlify(os.urandom(25)).decode("utf-8")
            aux_token = UserToken(token=token_string, user=self)
            return aux_token
        else:
            raise falcon.HTTPBadRequest(title=messages.quota_exceded, description=messages.maximum_tokens_exceded)

    @hybrid_property
    def json_model(self):
        return {
            "created_at": self.created_at.strftime(settings.DATETIME_DEFAULT_FORMAT),
            "username": self.username,
            "email": self.email,
            "name": self.name,
            "surname": self.surname,
            "birthdate": self.birthdate.strftime(
                settings.DATE_DEFAULT_FORMAT) if self.birthdate is not None else self.birthdate,
            "genere": self.genere.value,
            "phone": self.phone,
            "photo": self.photo_url,
            "points": self.points
        }
