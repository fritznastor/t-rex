import { useState, useCallback } from 'react';
import { getErrorMessage } from '../utils/common';

/**
 * Custom hook for handling API operations with loading, error, and success states
 */
export interface UseApiState<T = any> {
  data: T | null;
  loading: boolean;
  error: string | null;
  success: string | null;
}

export interface UseApiOperations<T = any> {
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setSuccess: (success: string | null) => void;
  clearMessages: () => void;
  setData: (data: T | null) => void;
}

export const useApi = <T = any>(initialData: T | null = null): [UseApiState<T>, UseApiOperations<T>] => {
  const [state, setState] = useState<UseApiState<T>>({
    data: initialData,
    loading: false,
    error: null,
    success: null,
  });

  const setLoading = useCallback((loading: boolean) => {
    setState(prev => ({ ...prev, loading, error: null, success: null }));
  }, []);

  const setError = useCallback((error: string | null) => {
    setState(prev => ({ ...prev, error, loading: false, success: null }));
  }, []);

  const setSuccess = useCallback((success: string | null) => {
    setState(prev => ({ ...prev, success, loading: false, error: null }));
  }, []);

  const clearMessages = useCallback(() => {
    setState(prev => ({ ...prev, error: null, success: null }));
  }, []);

  const setData = useCallback((data: T | null) => {
    setState(prev => ({ ...prev, data }));
  }, []);

  const operations: UseApiOperations<T> = {
    setLoading,
    setError,
    setSuccess,
    clearMessages,
    setData,
  };

  return [state, operations];
};

/**
 * Custom hook for async API operations
 */
export const useAsyncApi = <T = any>() => {
  const [state, operations] = useApi<T>();

  const execute = useCallback(async <R = T>(
    apiCall: () => Promise<{ data: R }>,
    successMessage?: string
  ): Promise<R | null> => {
    try {
      operations.setLoading(true);
      const response = await apiCall();
      operations.setData(response.data as any);
      if (successMessage) {
        operations.setSuccess(successMessage);
      }
      return response.data;
    } catch (error) {
      const errorMessage = getErrorMessage(error);
      operations.setError(errorMessage);
      return null;
    }
  }, [operations]);

  return {
    ...state,
    ...operations,
    execute,
  };
};

/**
 * Custom hook for form state management
 */
export interface UseFormOptions<T> {
  initialValues: T;
  onSubmit: (values: T) => Promise<void> | void;
  validate?: (values: T) => Partial<Record<keyof T, string>>;
}

export const useForm = <T extends Record<string, any>>({
  initialValues,
  onSubmit,
  validate,
}: UseFormOptions<T>) => {
  const [values, setValues] = useState<T>(initialValues);
  const [errors, setErrors] = useState<Partial<Record<keyof T, string>>>({});
  const [isSubmitting, setIsSubmitting] = useState(false);

  const setValue = useCallback((field: keyof T, value: any) => {
    setValues(prev => ({ ...prev, [field]: value }));
    // Clear error for this field when user starts typing
    if (errors[field]) {
      setErrors(prev => ({ ...prev, [field]: undefined }));
    }
  }, [errors]);

  const setFieldError = useCallback((field: keyof T, error: string) => {
    setErrors(prev => ({ ...prev, [field]: error }));
  }, []);

  const reset = useCallback(() => {
    setValues(initialValues);
    setErrors({});
    setIsSubmitting(false);
  }, [initialValues]);

  const handleSubmit = useCallback(async (e?: React.FormEvent) => {
    if (e) {
      e.preventDefault();
    }

    // Validate form
    if (validate) {
      const validationErrors = validate(values);
      if (Object.keys(validationErrors).length > 0) {
        setErrors(validationErrors);
        return;
      }
    }

    try {
      setIsSubmitting(true);
      setErrors({});
      await onSubmit(values);
    } catch (error) {
      console.error('Form submission error:', error);
    } finally {
      setIsSubmitting(false);
    }
  }, [values, validate, onSubmit]);

  return {
    values,
    errors,
    isSubmitting,
    setValue,
    setFieldError,
    reset,
    handleSubmit,
  };
};
