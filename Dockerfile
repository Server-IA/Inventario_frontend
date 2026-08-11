# Use a Node.js image for building and running
FROM node:20

# Set the working directory
WORKDIR /app

# Accept build-time env vars (Vite inlines import.meta.env at build time)
ARG VITE_BACKEND_URI=https://dev.api.inmero.co/coagronet
ENV VITE_BACKEND_URI=$VITE_BACKEND_URI

# Copy package.json and package-lock.json
COPY package*.json ./

# Install all dependencies (including 'serve')
RUN npm install

# Copy the rest of the application source code
COPY . .

# Build the application for production
RUN npm run build

# Expose the port the server will run on (default for 'serve' is 3000)
EXPOSE 3000

# Command to run the application using the 'serve' package
# This will serve the 'dist' directory
CMD ["npm", "start"]
