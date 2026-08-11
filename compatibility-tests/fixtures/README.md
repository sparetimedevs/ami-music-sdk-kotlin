# Frozen JSON fixtures

Each `vX.Y.Z/` directory contains JSON exactly as serialized by that SDK version. These files are
**frozen**: never edit or regenerate them. They are the immutable "truth" that the compatibility
tests check every SDK version against — editing them would silently erase the compatibility
guarantee they provide.

When a new release is cut, snapshot its serialized JSON into a new `vX.Y.Z/` directory as part of
the release checklist. That is how the backward/forward compatibility matrix grows.

Note: until there are real releases, `v0.0.1/` and `v0.0.2/` contain copies of the same current
examples, as placeholders that showcase the structure.
