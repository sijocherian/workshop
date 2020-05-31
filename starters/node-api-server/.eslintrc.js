module.exports = {
    env: {
        browser: true,
        es6: true,
        node: true,
    },
    extends: ['airbnb', 'plugin:node/recommended', 'plugin:prettier/recommended'],
    globals: {
        Atomics: 'readonly',
        SharedArrayBuffer: 'readonly',
    },
    parser: "babel-eslint",
    parserOptions: {
        ecmaFeatures: {
            jsx: true,
        },
        ecmaVersion: 2018,
        sourceType: 'module',
    },
    plugins: ['react', 'prettier'],
    rules: {
        'prettier/prettier': 'off',
        'spaced-comment': ['error', 'always', { exceptions: ['*'] }],
        indent: ['error', 4],
        'max-lines-per-function': ['warn', 100],
        'max-depth': ['warn', 5],
        complexity: [
            'warn',
            {
                max: 5,
            },
        ],
        'max-len': ['warn', 120],
        'max-params': ['warn', 7],
        'max-lines-per-function': ['warn', 120],
        "react/jsx-filename-extension": [1, {"extensions": [".js", ".jsx"]}],
        'node/no-unsupported-features/es-syntax': 'off',
        'react/jsx-indent': 'off',
        'react/jsx-indent-props': 'off',
        'react/destructuring-assignment': 'off',
        'react/state-in-constructor': 'off',
        'react/prop-types': 'off',
        'react/no-access-state-in-setstate': 'off',
        'react/jsx-props-no-spreading': 'off',
        'react/forbid-prop-types': 'off',
        'no-shadow': 'off',
    },
};
