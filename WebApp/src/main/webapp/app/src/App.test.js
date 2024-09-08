// Unit test for the App component that checks if elements are rendered correctly using React Testing Library.
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import App from './App';

test('renders header links', () => {
  render(<App />);
  const linkGuides = screen.getByText(/Guides/i);
  expect(linkGuides).toBeInTheDocument();
})

test('renders header logo', () => {
  render(<App />);
  const logoImage = screen.getByAltText(/Logo/i);
  expect(logoImage).toBeInTheDocument();
})

test('renders header animation', () => {
  render(<App />);
  const animation = screen.getByAltText(/Running Horse/i);
  expect(animation).toBeInTheDocument();
});