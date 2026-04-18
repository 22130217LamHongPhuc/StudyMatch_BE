// File: src/services/api-config.js ho?c api-config.ts
const API_CONFIG = {
  // API Base URL
  baseURL: 'http://localhost:8081/api',
  // Endpoints
  endpoints: {
    onboarding: {
      submit: '/onboarding/submit'
    }
  },
  // Headers
  headers: {
    'Content-Type': 'application/json'
  },
  // Timeouts
  timeout: 30000, // 30 seconds
  // Retry Config
  maxRetries: 3,
  retryDelay: 1000 // 1 second
};
// Onboarding Service
class OnboardingService {
  constructor(config = API_CONFIG) {
    this.baseURL = config.baseURL;
    this.timeout = config.timeout;
    this.maxRetries = config.maxRetries;
    this.retryDelay = config.retryDelay;
  }
  /**
   * Submit student onboarding
   * @param {Object} studentData - Student onboarding data
   * @param {Long} userId - User ID (optional, defaults to 1)
   * @returns {Promise<OnboardingResponse>}
   */
  async submitOnboarding(studentData, userId = null) {
    const url = API_CONFIG.baseURL + API_CONFIG.endpoints.onboarding.submit;
    const headers = {
      ...API_CONFIG.headers,
      ...(userId && { 'X-User-Id': userId })
    };
    const config = {
      method: 'POST',
      headers: headers,
      body: JSON.stringify(studentData),
      timeout: this.timeout
    };
    return this._fetchWithRetry(url, config);
  }
  /**
   * Fetch with retry logic
   */
  async _fetchWithRetry(url, config, attempt = 1) {
    try {
      const response = await Promise.race([
        fetch(url, config),
        new Promise((_, reject) =>
          setTimeout(() => reject(new Error('Request timeout')), config.timeout)
        )
      ]);
      if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Request failed');
      }
      return await response.json();
    } catch (error) {
      if (attempt < this.maxRetries) {
        await new Promise(resolve => setTimeout(resolve, this.retryDelay * attempt));
        return this._fetchWithRetry(url, config, attempt + 1);
      }
      throw error;
    }
  }
}
// Export
export default new OnboardingService();
export { OnboardingService };
