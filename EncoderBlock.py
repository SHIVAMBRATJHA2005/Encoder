# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.

"""Transformer encoder block."""

import torch
from torch import nn


class EncoderBlock(nn.Module):
	"""A transformer encoder block with pre-layer normalization."""

	def __init__(
		self,
		embed_dim: int,
		num_heads: int,
		ff_dim: int | None = None,
		dropout: float = 0.1,
	) -> None:
		super().__init__()
		if embed_dim % num_heads != 0:
			raise ValueError("embed_dim must be divisible by num_heads")

		ff_dim = ff_dim or embed_dim * 4
		self.norm1 = nn.LayerNorm(embed_dim)
		self.attention = nn.MultiheadAttention(
			embed_dim,
			num_heads,
			dropout=dropout,
			batch_first=True,
		)
		self.dropout1 = nn.Dropout(dropout)
		self.norm2 = nn.LayerNorm(embed_dim)
		self.feed_forward = nn.Sequential(
			nn.Linear(embed_dim, ff_dim),
			nn.GELU(),
			nn.Dropout(dropout),
			nn.Linear(ff_dim, embed_dim),
		)
		self.dropout2 = nn.Dropout(dropout)

	def forward(
		self,
		x: torch.Tensor,
		attention_mask: torch.Tensor | None = None,
		key_padding_mask: torch.Tensor | None = None,
	) -> torch.Tensor:
		"""Encode a batch of sequences shaped ``(batch, sequence, embed_dim)``."""
		normalized = self.norm1(x)
		attended, _ = self.attention(
			normalized,
			normalized,
			normalized,
			attn_mask=attention_mask,
			key_padding_mask=key_padding_mask,
			need_weights=False,
		)
		x = x + self.dropout1(attended)
		return x + self.dropout2(self.feed_forward(self.norm2(x)))
