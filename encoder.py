import tensorflow as tf
from tensorflow.keras import layers, Model

# Input
input_layer = layers.Input(shape=(784,))

# Encoder
x = layers.Dense(256, activation='relu')(input_layer)
x = layers.Dense(128, activation='relu')(x)
x = layers.Dense(64, activation='relu')(x)

# Latent representation
encoded = layers.Dense(32, activation='relu', name='latent_space')(x)

# Encoder model
encoder = Model(input_layer, encoded, name="Encoder")

encoder.summary()